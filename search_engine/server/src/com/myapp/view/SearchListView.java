package com.myapp.view;
import java.util.*;

/**
 * Construct the viewing information of search result list for jsp
 * The container of SearchObjectView
 * @author Haoyun Qiu 
 *
 */
public class SearchListView
{
	private ArrayList<SearchObjectView> results;
	
	public SearchListView()
	{
		results = new ArrayList<SearchObjectView>();
	}
			
	public void addResult(SearchObjectView res)
	{
		results.add(res);
	}
	
	public ArrayList<SearchObjectView> getResults()
	{
		return results;
	}
	
	public SearchObjectView get(int idx)
	{
		return results.get(idx);
	}
	
	public int size()
	{
		return results.size();
	}
}
