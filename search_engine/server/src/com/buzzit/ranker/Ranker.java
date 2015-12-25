package com.buzzit.ranker;

import com.buzzit.indexer.Indexer;
import com.buzzit.indexer.wiki.KnowledgeEntity;
import com.buzzit.indexer.wiki.KnowledgeGraph;
import com.buzzit.pagerank.PageRankRecords;
import scala.Tuple2;

import java.util.*;

/**
 *
 */
public class Ranker {
    private Indexer indexer;
    private PageRankRecords pageRankRecords;

    public Ranker() {
        indexer = new Indexer();
        System.out.println("Starting reading corpus and computing tf-idf...");
        indexer.computeIndexer();

        pageRankRecords = PageRankRecords.getInstance();
    }

    public List<String> query(String query) {
        List<String> result = new ArrayList<>();
        // retrieves top 100 query results by default.
        List<Tuple2<String, Double>> queryResult = indexer.getQueryResult(query);

        // combine results with page ranker
        queryResult = sortWithPageRank(queryResult);

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

    protected List<Tuple2<String, Double>> sortWithPageRank(List<Tuple2<String, Double>> queryResult) {
        for (int i=0; i<queryResult.size(); ++i) {
            String url = queryResult.get(i)._1;
            double weight = queryResult.get(i)._2;
            weight = weight * pageRankRecords.getUrlWeight(url);
            queryResult.set(i, new Tuple2<>(url, weight));
        }
        Collections.sort(queryResult, DocWeightComparator.INSTANCE);
        return queryResult;
    }

    private static class DocWeightComparator implements Comparator<Tuple2<String, Double>>{
        static DocWeightComparator INSTANCE = new DocWeightComparator();
        @Override
        public int compare(Tuple2<String, Double> o1, Tuple2<String, Double> o2) {
            return Double.compare(o2._2(), o1._2());
        }
    }

    public static void main(String args[]) {
        Ranker ranker = new Ranker();
        ranker.run();
    }
}
