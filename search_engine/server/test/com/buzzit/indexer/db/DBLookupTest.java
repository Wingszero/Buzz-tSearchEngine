package com.buzzit.indexer.db;

import com.myapp.worker.db.WebPageEntity;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 *
 */
public class DBLookupTest {
    @Test
    public void testDBLookup() {
        DBLookup lookup = DBLookup.getInstance();
        List<String> urls = new ArrayList<>();
        urls.add("https://movielens.org");
        urls.add("https://floridastate.rivals.com");

        List<WebPageEntity> pages = lookup.getPages(urls);
        assertEquals("https://movielens.org", pages.get(0).getUrl());
        assertEquals("https://floridastate.rivals.com", pages.get(1).getUrl());
    }
}