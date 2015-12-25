package com.myapp.worker;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashSet;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.myapp.worker.utils.Common;
import com.myapp.worker.utils.NormalizeURL;

public class LinkExtractor
{
	/**
	 * filterList for crawling 
	 * allList for PageRank
	 * @param content
	 * @param base_url
	 * @return clean content
	 */
	public static void extractLinks(Document doc, String base_url, HashSet<String>filterSet, ArrayList<String>allList) 
	{
		Elements links = doc.select("a");
		for(Element  link: links)
		{
			String url = link.attr("abs:href");

			// filter mailto url here 
			if(!URLFilter.filterMailTo(url)) 
			{
				continue;
			}

			url = NormalizeURL.normalize(url);
			if(url.isEmpty()) continue;

			allList.add(url);	
			
			if(!url.equals(base_url))
			{
				filterSet.add(url);
			}
		}
	}

	/*
	public static boolean isFilter(String url)
	{
		if(url.endsWith(".html"))
		{
			return false;
		}
		return url.endsWith(".xml") || url.endsWith(".jpg") || url.endsWith(".css") || url.endsWith(".png") || url.endsWith(".ico");
	}
	*/

	public static void print(String s)
	{
		System.out.println(s);
	}
}
