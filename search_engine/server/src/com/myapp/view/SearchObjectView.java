package com.myapp.view;
import java.util.*;

/**
 * Construct the viewing information of each search result for jsp
 * @author Haoyun Qiu 
 *
 */
public class SearchObjectView 
{
	private String title;
	private String url;
	private String abst;

	public SearchObjectView()
	{
		
	}
	
	public SearchObjectView(String title,String url, String abst)
	{
		this.title = title;
		this.url = url;
		this.abst = abst;
	}
	
	public String getTitle()
	{
		return title;
	}
	
	public String getUrl()
	{
		return url;
	}	
	
	public String getAbstract()
	{
		return abst;
	}	
}
