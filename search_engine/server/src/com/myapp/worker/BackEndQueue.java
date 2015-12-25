package com.myapp.worker;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.myapp.worker.utils.NormalizeURL;

/**
 * The back-end Queue for URLFrontier which are the urls for crawling now..
 * 
 * @author Haoyun 
 *
 */
public class BackEndQueue 
{
	/**
	 * key: a host
	 * value: the urls belongs to this host
	 */
	private ConcurrentHashMap<Integer, ArrayList<String>>indexQueue = new ConcurrentHashMap<Integer, ArrayList<String>>();
	
	public BackEndQueue(int activeHostNum)
	{
		for(int i = 0; i < activeHostNum; ++i)
		{
			indexQueue.put(i, new ArrayList<String>());
		}
	}

	public void put(Integer index, String url) 
	{
		ArrayList<String>q = indexQueue.get(index); 
		q.add(url);
	}
	
	public ArrayList<String> get(Integer index) 
	{
		return indexQueue.get(index);
	}

	public ConcurrentHashMap<Integer, ArrayList<String>> getQueue()
	{
		return indexQueue;
	}

	/**
	 * @return
	 */
	public boolean isEmpty()
	{
		return indexQueue.isEmpty(); 
	}

	/**
	 * not the size of mapping, but the size of all url queue's total size
	 * @return
	 */
	public int size() 
	{
		int t = 0;
		for(ArrayList<String>q: indexQueue.values())
		{
			t += q.size();
		}
		return t;
	}

	public String getStatString() 
	{
		return null;
	}
}
