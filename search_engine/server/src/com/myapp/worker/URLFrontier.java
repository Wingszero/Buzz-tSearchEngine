package com.myapp.worker;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

import com.myapp.worker.HostTimeHeap.HostTime;
import com.myapp.worker.utils.Const;
import com.myapp.worker.utils.NormalizeURL;
import com.myapp.worker.utils.SHA1HashFunction;

/**
 * 
 * @author Haoyun 
 *
 */
public class URLFrontier 
{
	/*
	 * Priority queue: 
	 * head: the earlist host can crawl 
	 */
	private HostTimeHeap  hostHeap; 

	/*
	 * extact links will be added to frontQueue first
	 */
	private BlockingQueue<String>frontQueue;

	/*
	 * key: host
	 * value: url queue belongs to this host 
	 */
	private BackEndQueue backQueue;

	/*
	 * host -> back-end queue index
	 */
	private ConcurrentHashMap<String, Integer>hostIndexMap = new ConcurrentHashMap<String, Integer>(); 

	private Worker crawl_ins;

	private int activeHostNum;
	private int curAssignBackQueueIdx;

	public URLFrontier(ArrayList<String> hosts, Worker crawler) 
	{
		long cur = System.currentTimeMillis();

		activeHostNum = crawler.getNumThreads() * 10;

		frontQueue = new LinkedBlockingQueue<String>(Const.MAX_FRONTQUEUE_SIZE);

		backQueue = new BackEndQueue(activeHostNum); 

		hostHeap = new HostTimeHeap();

		crawl_ins = crawler;

		curAssignBackQueueIdx = 0;

		for(String url: hosts)
		{
			String h = NormalizeURL.getDomainName(url); 
			//has host
			if(hostIndexMap.containsKey(h))
			{
				// find the back-queue index
				int queueIndex = hostIndexMap.get(h); 
				// enqueue
				backQueue.put(queueIndex, url);
			}
			else if(curAssignBackQueueIdx < activeHostNum)
			{
				hostHeap.add(h, cur);
				hostIndexMap.put(h, curAssignBackQueueIdx);
				backQueue.put(curAssignBackQueueIdx, url);
				curAssignBackQueueIdx ++;
			}
			else
			{
				try 
				{
					frontQueue.put(url);
				}
				catch (InterruptedException e) 
				{
					e.printStackTrace();
				}
			}
		}

		/*
		System.out.println("hostHeap size: " + hostHeap.size());
		System.out.println("hostIndexMap size: " + hostIndexMap.size());
		System.out.println(hostIndexMap);
		 */
	}

	public void printAll()
	{
		System.out.println("hostTimeQ: " + hostHeap.size());
		//System.out.println(hostSet);

		System.out.println("urlQueue:" + backQueue.size());
		//System.out.println(urlQueue.getQueue());

		System.out.println();
	}

	public int getHostIndex(String host)
	{
		if(host == null || host.isEmpty())
		{
			return -1;
		}
		return hostIndexMap.get(host);
	}

	/**
	 * 
	 * @return the earliest url can be crawled
	 */
	public String getNextURL()
	{
		//printAll();
		HostTime ht = hostHeap.poll();
		if(ht == null)
		{
			System.out.println("getNextUrl hostHeap poll is null !"); 
			return null;
		}

		long cur = System.currentTimeMillis();
		long next_time = ht.next_time; 
		long wait = next_time - cur;
		String host = ht.host;
		if(wait > 0)
		{
			try
			{
				System.out.println("crawl-delay: " + host + " " + wait);
				Thread.sleep(wait);
			}
			catch(Exception e)
			{
				System.out.println("getNextUrl Thread sleep Exception: " + host);
				return null;
			}
			//return null;
		}

		int index = getHostIndex(host); 
		if(index == -1)
		{
			System.out.println("[ERROR] getHostIndex is -1 host: " + host);
			return null;
		}
		ArrayList<String> urlList = backQueue.get(index); 

		if(urlList == null || urlList.isEmpty())
		{
			System.out.println("[ERROR]  getNextUrl null urlList isEmpty(): host " + host);
			return null;
		}	
		return urlList.get(0);
	}

	/**
	 * add a new url to queue by host
	 * @param url
	 */
	public void addNewUrl(String url) 
	{
		synchronized(this)
		{
			//back-end Queue entry not full
			if(curAssignBackQueueIdx < activeHostNum)
			{
				//print("jason hihi: curAssignBackQueueIdx " + curAssignBackQueueIdx);
				String h = NormalizeURL.getDomainName(url); 
				if(hostIndexMap.containsKey(h))
				{
					// find the back-queue index
					int queueIndex = hostIndexMap.get(h); 
					backQueue.put(queueIndex, url);
				}
				else
				{
					hostHeap.add(h, System.currentTimeMillis());
					hostIndexMap.put(h, curAssignBackQueueIdx);
					backQueue.put(curAssignBackQueueIdx, url);
					curAssignBackQueueIdx ++;
				}
				return;
			}
		}

		if(frontQueue.size() < Const.MAX_FRONTQUEUE_SIZE)
		{
			try
			{
				frontQueue.put(url);
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	}

	/**
	 * @return
	 */
	public boolean isEmpty()
	{
		return backQueue.isEmpty();
	}

	/**
	 * in secs
	 * @param host
	 * @return
	 */
	public Integer getHostDelay(String host) 
	{
		Integer d = crawl_ins.getHostDelay(host);
		return d == null ? Const.ROBOTS_DEFAULT_DELAY * 1000: d;
	}

	public static void print(String data)
	{
		System.out.println(data);
	}

	public int size() 
	{
		return backQueue.size();
	}

	public String getStatString() 
	{
		String status = "hostHeap size: " + hostHeap.size() + " ";
		status += ("Front-end queue size: " + frontQueue.size() + " "); 

		/*
		status += ("back-end queue: "); 
		for(int i = 0; i < activeHostNum; ++i)
		{
			System.out.println(backQueue.get(i).size());
		}
		 */
		return status;
	}

	public void updateAfterFetch(String url_str) 
	{
		String host = NormalizeURL.getDomainName(url_str); 
		int index = getHostIndex(host);
		if(index == -1)
		{
			System.out.println("[ERROR] getHostIndex is -1 host: " + host);
			return;
		}

		ArrayList<String>urlQueue = backQueue.get(index);

		if(urlQueue.isEmpty())
		{
			System.out.println("[ERROR] updateAfterFetch urlQueue.isEmpty host: " + host + " " + "index: " + index);
		}
		else
		{
			urlQueue.remove(0);
		}

		// refill back-end Queue from front-end Queue
		if(!urlQueue.isEmpty())
		{
			hostHeap.add(host, System.currentTimeMillis() + getHostDelay(host));
		}
		else
		{
			synchronized(this)
			{
				hostIndexMap.remove(host);
				while(!frontQueue.isEmpty())
				{
					String url = frontQueue.poll();
					String host2 = NormalizeURL.getDomainName(url); 
					// not contains host2
					if(!hostIndexMap.containsKey(host2))
					{
						hostIndexMap.put(host2, index);
						urlQueue.add(url);
						if(host2.equals(host))
						{
							hostHeap.add(host, System.currentTimeMillis() + getHostDelay(host));
						}
						else
						{
							hostHeap.add(host2, System.currentTimeMillis()); 
						}
						break;
					}
					else
					{
						int index2 = getHostIndex(host2);
						ArrayList<String>urlQueue2 = backQueue.get(index2);
						urlQueue2.add(url);
					}
				}
			}
		}
	}
}
