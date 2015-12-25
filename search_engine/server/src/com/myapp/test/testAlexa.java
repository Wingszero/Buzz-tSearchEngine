package com.myapp.test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;

import com.google.common.net.InternetDomainName;
import com.myapp.worker.HttpFetcher;
import com.myapp.worker.utils.Const;
import com.myapp.worker.seedsFetcher;

import junit.framework.TestCase;

public class testAlexa extends TestCase 
{
	@Test
	public void test1() throws URISyntaxException, IOException 
	{
		seedsFetcher.processUSTop();
	}

	private static void print(String s)
	{
		System.out.println(s);
	}

}
