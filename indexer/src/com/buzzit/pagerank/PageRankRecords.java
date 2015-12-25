package com.buzzit.pagerank;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.*;

/**
 *
 */
public class PageRankRecords {
    private static PageRankRecords records = null;
    private HashMap<String, Double> urlWeightMap;
    private PageRankRecords() {
        try {
            File file = new File("../testdir/small_pagerank_sorted");
            BufferedReader in = new BufferedReader(new FileReader(file));
            urlWeightMap = new HashMap<>();
            String line;
            while ((line = in.readLine()) != null) {
                String pair[] = line.split("\t");
                String url = pair[0];
                double weight = Double.parseDouble(pair[1]);
                urlWeightMap.put(url, weight);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static PageRankRecords getInstance() {
        if (records == null) {
            records = new PageRankRecords();
        }
        return records;
    }

    public double getUrlWeight(String url) {
        if (urlWeightMap.containsKey(url)) {
            return urlWeightMap.get(url);
        }
        return 0.15;
    }

    /// DEBUG

    public String[] getUrls() {
        return urlWeightMap.keySet().toArray(new String[urlWeightMap.keySet().size()]);
    }

}
