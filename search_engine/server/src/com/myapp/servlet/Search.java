package com.myapp.servlet;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Hashtable;
import java.util.*;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.buzzit.imagesearch.ImageNet;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.buzzit.SearchEngine;
import com.buzzit.indexer.wiki.KnowledgeEntity;
import com.buzzit.ranker.QueryPageEntity;
import com.buzzit.ranker.QueryResultItems;
import com.myapp.storage.DBWrapper;
import com.myapp.utils.Const;
import com.myapp.view.SearchListView;
import com.myapp.view.SearchObjectView;
import com.myapp.utils.Const;

public class Search extends HttpServlet
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -2835467381465359391L;
	private DBWrapper db; 
	private SearchEngine se;
	private static long filename = 0;
	
	public Search(){
		super();
		se = SearchEngine.getInstance();
	}
	
	public void initDB()
	{
		if(db != null) return;
		try 
		{
			db = new DBWrapper();
		}
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	}

	public void init()
	{
		/*Load first time, avoid latency*/
		//SQLDBWrapper.getConnection();
		
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response) 
	{
		String url = request.getServletPath();
		if(url.equals(Const.SEARCH_RESULT_URL))
		{
			handleSearchResult(request, response);
		}
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response) 
	{
		String url = request.getServletPath();
		if(url.equals(Const.SEARCH_URL) || url.equals(Const.HOME_URL))
		{
			handleSearchHomePage(request, response);
		}
		else if(url.equals(Const.VOICE_SEARCH))
		{
			handleVoiceSearchGet(request, response);
		}
		else
		{
			ServletCommon.redirect404(request, response);
		}
	}

	private void handleSearchHomePage(HttpServletRequest request, HttpServletResponse response) 
	{
		String location = "htmls/home.html";
		ServletCommon.forwardRequestDispatch(request, response, location);
	}

	private void handleVoiceSearchGet(HttpServletRequest request, HttpServletResponse response) 
	{
		String location = "/htmls/speech/speech_sample.html";
		ServletCommon.forwardRequestDispatch(request, response, location);	
	}

	private void handleSearchResult(HttpServletRequest request, HttpServletResponse response) {
		//for test
		/*
		SearchListView slv = new SearchListView();
		
		for(int i = 0; i < 3; ++i)
		{
			String title = "\"Hello, World!\" program - Wikipedia, the free encyclopedia";
			String url = "https://en.wikipedia.org/wiki/%22Hello,_World!%22_program";
			String abst = "A \"Hello, World!\" program is a computer program that outputs \"Hello, World!\" on a display device. "
					+ "Being a very simple program in most programming languages, ...";
			SearchObjectView sov = new SearchObjectView(title, url, abst);
			slv.addResult(sov);
		}
		*/

		long queryTime;
		int k = 10;
		QueryResultItems queryResultItems;
		KnowledgeEntity entity;
    String query = null;
    
    
    // MARK:
    query = request.getParameter("SEARCH_KEY");
    if (query != null) {
      if (query != null && query.length() > 0) {
        queryTime = System.currentTimeMillis();
        queryResultItems = se.query(query);
        queryTime = System.currentTimeMillis() - queryTime;
        
        entity = queryResultItems.getKnowledgeEntity();
        
        System.out.println("Search: " + query + " took " + (double) queryTime / 1000 + "s.");
        int counter = 0;
        List<QueryPageEntity> pageList = null;
        if (queryResultItems != null) {
          pageList = queryResultItems.getWebPageEntities();
          for (QueryPageEntity page : pageList) {
            System.out.println((counter + 1) + "." + page.getTitle());
            System.out.println(page.getUrl());
            System.out.println(page.getAbs());
            counter++;
            if (counter >= k) break;
          }
        }
        if (counter == 0) {
          System.out.println("Query Not found");
        }
        
        System.out.println("");
        if (entity != null) {
          System.out.println("Title: " + entity.getTitle());
          System.out.println("Url: " + entity.getUrl());
          System.out.println("Content: " + entity.getContent());
        } else System.out.println("Wiki Not found");
        request.setAttribute("QueryResultItems", queryResultItems);
        request.setAttribute("plain_query", query);
        //request.setAttribute("SearchListView", slv);
        String shouldReturnJson = request.getParameter("RETURN_JSON");
        if (shouldReturnJson != null && "yes".equals(shouldReturnJson) && pageList != null) {
          // Return JSON. For mobile usage.
          try {
            PrintWriter w = response.getWriter();
            w.println("{ \"time-spent\":" + (double)queryTime/1000 + ",");
            w.println("\"total-num-of-results\":" + pageList.size() + ",");
            w.println("\"num-of-results-returned\":" + counter + ",");
            w.println("\"results\":[");
            for (int i = 0; i < counter; i++) {
              QueryPageEntity page = pageList.get(i);
              w.println("{\"title\":\"" + page.getTitle().replace("\"", "\\\"") + "\",");
              w.println("\"url\":\"" + page.getUrl().replace("\"", "\\\"") + "\",");
              w.print("\"content\":\"" + page.getAbs().replace("\"", "\\\"") + "\"}");
              if (i < counter - 1) {
                w.println(",");
              }
            }
            w.println("]}");
          } catch (IOException e) {
            e.printStackTrace();
          }
        } else {
          String location = "jsp/QueryResult.jsp";
          ServletCommon.forwardRequestDispatch(request, response, location);
        }
      }
    }

    
    
		try {
			if (ServletFileUpload.isMultipartContent(request)) {
				final FileItemFactory factory = new DiskFileItemFactory();
				final ServletFileUpload upload = new ServletFileUpload(factory);

				final List<FileItem> items = upload.parseRequest(request);
        
				for (FileItem item : items) {
					if (item.isFormField()) {
						System.out.println("@@@@@@ item is form field.");
						String name = item.getFieldName();
            query = item.getString();
            if (query == null || query.length() < 1) {
              query = request.getParameter("SEARCH_KEY");
            }
						System.out.println("@@@@@query is " + query + "  field name is " + name);
						if (query != null && query.length() > 0) {
							queryTime = System.currentTimeMillis();
							queryResultItems = se.query(query);
							queryTime = System.currentTimeMillis() - queryTime;

							entity = queryResultItems.getKnowledgeEntity();

							System.out.println("Search: " + query + " took " + (double) queryTime / 1000 + "s.");
							int counter = 0;
							List<QueryPageEntity> pageList = null;
							if (queryResultItems != null) {
								pageList = queryResultItems.getWebPageEntities();
								for (QueryPageEntity page : pageList) {
									System.out.println((counter + 1) + "." + page.getTitle());
									System.out.println(page.getUrl());
									System.out.println(page.getAbs());
									counter++;
									if (counter >= k) break;
								}
							}
							if (counter == 0) {
								System.out.println("Query Not found");
							}

							System.out.println("");
							if (entity != null) {
								System.out.println("Title: " + entity.getTitle());
								System.out.println("Url: " + entity.getUrl());
								System.out.println("Content: " + entity.getContent());
							} else System.out.println("Wiki Not found");
							request.setAttribute("QueryResultItems", queryResultItems);
							request.setAttribute("plain_query", query);
							//request.setAttribute("SearchListView", slv);
							String shouldReturnJson = request.getParameter("RETURN_JSON");
							if (shouldReturnJson != null && "yes".equals(shouldReturnJson) && pageList != null) {
								// Return JSON. For mobile usage.
								try {
									PrintWriter w = response.getWriter();
									w.println("{ \"time-spent\":" + (double)queryTime/1000 + ",");
									w.println("\"total-num-of-results\":" + pageList.size() + ",");
									w.println("\"num-of-results-returned\":" + counter + ",");
									w.println("\"results\":[");
									for (int i = 0; i < counter; i++) {
										QueryPageEntity page = pageList.get(i);
										w.println("{\"title\":\"" + page.getTitle() + "\",");
										w.println("\"url\":\"" + page.getUrl() + "\",");
										w.print("\"content\":\"" + page.getAbs() + "\"}");
										if (i < counter - 1) {
											w.println(",");
										}
									}
									w.println("]}");
								} catch (IOException e) {
									e.printStackTrace();
								}
							} else {
								String location = "jsp/QueryResult.jsp";
								ServletCommon.forwardRequestDispatch(request, response, location);
							}
						}
					} else {
						System.out.println("@@@@@@@@ item is uploaded file");
						String contentType = item.getContentType();
						System.out.println("@@@@@@file type is " + contentType);
						if (!contentType.startsWith("image")) {
							System.out.println("@@@@@@illegal file format");
							break;
						}
						filename++;
						String type = contentType.split("/")[1];
						String filePath = filename + "." + type;
						System.out.println("@@@@@@@file path is " + filePath);
						File file = new File(filePath);
						try {
							item.write(file);
						} catch (Exception e) {
							e.printStackTrace();
						}
						queryTime = System.currentTimeMillis();
						queryResultItems = se.queryImage(file);
						queryTime = System.currentTimeMillis() - queryTime;
						entity = queryResultItems.getKnowledgeEntity();
						System.out.println("Search took " + (double) queryTime / 1000 + "s.");
						if (queryResultItems.getWebPageEntities().size() == 0) {
							System.out.println("Query Not found");
						}
						System.out.println("");
						if (entity != null) {
							System.out.println("Title: " + entity.getTitle());
							System.out.println("Url: " + entity.getUrl());
							System.out.println("Content: " + entity.getContent());
						} else System.out.println("Wiki Not found");
						request.setAttribute("QueryResultItems", queryResultItems);
						request.setAttribute("image_query", queryResultItems.getQuery());
						//request.setAttribute("SearchListView", slv);
						String location = "jsp/QueryResult.jsp";
						//System.out.println("start forwarding to jsp");
						ServletCommon.forwardRequestDispatch(request, response, location);
					}

				}
			}
		}catch (Exception e){
			e.printStackTrace();
		}
	}
}

/**
 JSON sample (validated)
 activated when ?RETURN_JSON=yes
 {
  "time-spent": 10,
  "total-num-of-results": 20,
  "num-of-results-returned": 2,
  "results": [
    {
      "title": "this is the title",
      "url": "https://www.foo.bar",
      "content": "lorem ipsum"
    }, {
      "title": "this is the title - No.2",
      "url": "https://www.foo.bar2",
      "content": "lorem ipsum 2"
    }
  ]
 }
 */

