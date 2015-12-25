package com.buzzit.pagerank;

import org.junit.Test;

import java.util.Random;

import static org.junit.Assert.*;

/**
 *
 */
public class PageRankRecordsTest {
    @Test
    public void testPerformance() {
        long time = System.currentTimeMillis();
        PageRankRecords records = PageRankRecords.getInstance();
        time = System.currentTimeMillis() - time;

        String urls[] = records.getUrls();

        System.out.println("Reading " + urls.length + " records took " + (double)time / 1000 + "s.");

        long tmp;
        time = 0;
        Random random = new Random();
        for (int i=0; i<100000; ++i) {
            String urlToLookup = urls[random.nextInt(urls.length)];
            tmp = System.currentTimeMillis();
            records.getUrlWeight(urlToLookup);
            time += (System.currentTimeMillis() - tmp);
        }
        System.out.println("Retrieving 100000 random url weights took " + (double)time / 1000 + "s.");
    }
}