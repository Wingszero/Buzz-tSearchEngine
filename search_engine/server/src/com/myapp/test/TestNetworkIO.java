package com.myapp.test;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URL;
import java.net.UnknownHostException;
import org.junit.Test;


import junit.framework.TestCase;


/*
 * test GET/HEAD and so on Network functions 
 * basically edu.upenn.cis455.crawler.HttpClient;
 */
public class TestNetworkIO extends TestCase 
{
	
	@Test
	public void testHEAD() throws UnknownHostException, IOException 
	{
		/*
		String url = "https://dbappserv.cis.upenn.edu/crawltest.html";
		url = "http://www.nba.com/";
		long date = HttpClient.getUrlLastModified(url); 
		print("lastModified: " + date);
		*/
	}
	
	@Test
	public void testUrl() throws UnknownHostException, IOException 
	{
		/*
		String url = "https://dbappserv.cis.upenn.edu/crawltest.html";
		URL addr = new URL(url);
		print(addr.getPath());
		print(addr.getProtocol());
		print(addr.getHost());
		*/
	}
	
	private static void print(String s)
	{
		System.out.println(s);
	}
}


