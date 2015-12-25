package com.buzzit.ranker;

import com.buzzit.indexer.Indexer;
import com.buzzit.indexer.wiki.KnowledgeEntity;
import com.buzzit.indexer.wiki.KnowledgeGraph;
import scala.Tuple2;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 *
 */
public class Ranker {
    private Indexer indexer;

    public Ranker() {
        indexer = new Indexer();
        System.out.println("Starting reading corpus and computing tf-idf...");
        indexer.computeIndexer();
    }

    public List<String> query(String query) {
        List<String> result = new ArrayList<>();
        // retrieves top 100 query results by default.
        List<Tuple2<String, Double>> queryResult = indexer.getQueryResult(query);

        // TODO: combine results with page ranker
        // ...
        for (Tuple2<String, Double> pair : queryResult) {
            result.add(pair._1);
        }

        return result;
    }

    public List<String> query(String query, int start, int end) {
        List<String> result = query(query);
        if (start >= result.size()) return null;
        if (end >= result.size()) end = result.size();
        return result.subList(start, end);
    }

    public void run() {
        long queryTime;
        int k = 10;
        String query;
        Scanner reader = new Scanner(System.in);
        List<String> queryResult;
        while (true) {
            System.out.print("Please input your query: ");
            query = reader.nextLine();

            queryTime = System.currentTimeMillis();
            queryResult = query(query, 0, k);
            queryTime = System.currentTimeMillis() - queryTime;

            System.out.println("Search: " + query + " took " + (double)queryTime/1000 + "s.");
            int resultNo = 1;
            if (queryResult != null) {
                for (String docTitle : queryResult) {
                    System.out.println(resultNo + ". " + docTitle);
                    resultNo++;
                }
            } else System.out.println("Not found");
        }
    }

    public static void main(String args[]) {
        Ranker ranker = new Ranker();
        ranker.run();
    }
}
