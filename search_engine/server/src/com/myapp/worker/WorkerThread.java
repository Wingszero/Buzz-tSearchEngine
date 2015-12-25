package com.myapp.worker;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

//import com.myapp.worker.aws.MessageSender;
import com.myapp.worker.db.WebPageEntity;
import com.myapp.worker.utils.Common;
import com.myapp.worker.utils.Const;
import com.myapp.worker.utils.NormalizeURL;
import com.myapp.worker.utils.Robots.RobotsTxtInfo;

public class WorkerThread implements Runnable 
{
	private Worker crawl_ins; 

	private boolean running = true;

	public WorkerThread(Worker c)
	{
		crawl_ins = c;
	}

	@Override
	public void run() 
	{
		while(isRunning())
		{
			//long st = System.currentTimeMillis();
			String url_str  = getNextUrl();
			if(url_str == null) 
			{
				try 
				{
					Thread.sleep(1000);
					continue;
				} 
				catch (InterruptedException e) 
				{
					continue;
				}
			}

			//check disallow links, may cause fetch robots.txt
			boolean ok = crawl_ins.isAllowed(url_str); 
			if(ok == false) continue;

			//crawled before
			WebPageEntity page = crawl_ins.getWebPage(url_str);
			if(page != null)
			{
				continue;
			}

			// fetch html
			String rawPage = HttpFetcher.fetchHtml(url_str, 0);

			// update url-frontier
			crawl_ins.updateURLFrontierAfterFetch(url_str);

			// parse html to DOM
			Document doc = Jsoup.parse(rawPage, url_str);
		
			// process canonical url
			String canonical_url = getCanonicalLink(doc);
			if(!canonical_url.isEmpty() && !url_str.equals(canonical_url))
			{
				//print("find canonical_url: " + canonical_url + " drop: " + url_str);
				crawl_ins.processLink(canonical_url);
				continue;
			}

			//clean tag
			String content = ContentExtractor.process(doc); 

			// content seen
			if(!crawl_ins.processContentSeen(content))
			{
				continue;
			}

			// stat
			crawl_ins.statPage(url_str, rawPage);

			// filterList: for later crawl
			HashSet<String>filterSet = new HashSet<String>();

			// allList: for PageRank
			ArrayList<String>allList = new ArrayList<String>();

			//extract external links, Malformed URL will be filtered 
			LinkExtractor.extractLinks(doc, url_str, filterSet, allList);

			//store
			crawl_ins.storeWebPage(url_str, extractTitle(doc), content, allList);

			// Filter----> HostSplitter ----> DUE ----------> URL Frontier
			crawl_ins.processLinks(filterSet);
		}
		print("Thread end:"  + Thread.currentThread().getName()); 
	}

	/**
	 * extract href of the canonical tag
	 * @param doc
	 * @return
	 */
	private String getCanonicalLink(Document doc)
	{
		Elements links = doc.select("link");
		for(Element link: links)
		{
			if(link.attr("rel").equals("canonical"))
			{
				return NormalizeURL.normalize(link.attr("href"));	
			}
		}
		return "";
	}

	private String extractTitle(Document doc) 
	{
		String title = "";
		try
		{
			title = doc.select("title").text();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return title;
	}

	private String getNextUrl() 
	{
		return crawl_ins.getNextUrl();
	}

	private boolean isRunning() 
	{
		return running; 
	}

	public void Stop()
	{
		running = false;
	}

	public static void print(String data)
	{
		System.out.println(data);
	}
}
