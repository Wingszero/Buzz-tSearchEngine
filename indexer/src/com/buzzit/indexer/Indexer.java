package com.buzzit.indexer;


import scala.Tuple2;

import java.util.*;

public class Indexer {
    private TfIdf tfIdf;
    //private List<Tuple2<Double, String>> queryResult;
    //private String currentQuery = null;

    public Indexer() {
    }

    public void computeIndexer() {
        tfIdf = new TfIdf();
        System.out.println("Read from: " + ConfigLoader.getHDFS() + ConfigLoader.getCorpusPath());
        tfIdf.compute(ConfigLoader.getHDFS() + ConfigLoader.getCorpusPath());
        //compute.saveToFile(ConfigLoader.getHDFS() + ConfigLoader.getTfidfPath());
    }

    public List<Tuple2<String, Double>> getQueryResult(String query) {
        /*
        if (query.compareTo(currentQuery) != 0) {
            currentQuery = query;
            queryResult = compute.getQueryResult(query);
        }
        */
        return tfIdf.getQueryResult(query);
    }

    public List<Tuple2<String, Double>> getQueryResult(String query, int start, int end) {
        List<Tuple2<String, Double>> queryResult = getQueryResult(query);
        if (end > queryResult.size()) {
            end = queryResult.size();
        }
        return getQueryResult(query).subList(start, end);
    }

    public List<Tuple2<String, Double>> getQueryResult(String query, Double thres) {
        List<Tuple2<String, Double>> queryResult = getQueryResult(query);
        int i;
        for (i=0; i<queryResult.size(); ++i) {
            if (queryResult.get(i)._2() < thres) break;
        }
        return queryResult.subList(0, i);
    }
    /**
     * Test run computing tf-idf from test directory.
     */
    protected void runTest() {
        tfIdf = new TfIdf();
        /*
        List<Tuple2<Tuple2<String, String>, Double>> weight = compute.compute(".." + ConfigLoader.getCorpusPath()).collect();
        for(Tuple2<Tuple2<String,String>, Double> t: weight){
            System.out.println("keyword: "+t._1()._1()+", doc: "+t._1()._2()+", weight: "+t._2());
        }
        */
    }

    protected void run() {
        // Load/compute tf-idf.
        System.out.println("Starting reading corpus and computing tf-idf...");
        long time = System.currentTimeMillis();
        computeIndexer();
        time = System.currentTimeMillis() - time;
        System.out.println("Finished reading corpus and computing tf-idf.");
        System.out.println("Processing took " + (double)time/1000 + "s.");

        int k = 10;
        String query;
        Scanner reader = new Scanner(System.in);
        List<Tuple2<String, Double>> queryResult;
        while (true) {
            System.out.print("Please input your query: ");
            query = reader.nextLine();
            time = System.currentTimeMillis();
            queryResult = getQueryResult(query, 0, k);
            time = System.currentTimeMillis() - time;
            System.out.println("Search took " + (double)time/1000 + "s.");
            int resultNo = 1;
            for (Tuple2<String, Double> docTitle : queryResult) {
                System.out.println(resultNo + ". " + docTitle._1());
                resultNo++;
            }
            System.out.println("");
        }
    }

    public static void main(String[] args) {
        new Indexer().run();
    }
}
