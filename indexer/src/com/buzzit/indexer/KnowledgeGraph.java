package com.buzzit.Indexer;

import scala.Tuple2;

import java.util.List;

/**
 * Return entity lookup based on wiki corpus.
 */
public class KnowledgeGraph {
    private TfIdf tfIdf;

    public KnowledgeGraph() {
    }

    public void computeKnowledgeGraph() {
        tfIdf = new TfIdf();
        System.out.println("Read from: " + ConfigLoader.getHDFS() + ConfigLoader.getWikiPath());
        tfIdf.tfIdf(ConfigLoader.getHDFS() + ConfigLoader.getWikiPath());
    }

    public List<Tuple2<Double, String>> getQueryResult(String query) {
        /*
        if (query.compareTo(currentQuery) != 0) {
            currentQuery = query;
            queryResult = tfIdf.getQueryResult(query);
        }
        */
        return tfIdf.getQueryResult(query);
    }

    public List<Tuple2<Double, String>> getQueryResult(String query, int start, int end) {
        List<Tuple2<Double, String>> queryResult = getQueryResult(query);
        if (end > queryResult.size()) {
            end = queryResult.size();
        }
        return getQueryResult(query).subList(start, end);
    }

    public List<Tuple2<Double, String>> getQueryResult(String query, Double thres) {
        List<Tuple2<Double, String>> queryResult = getQueryResult(query);
        int i;
        for (i=0; i<queryResult.size(); ++i) {
            if (queryResult.get(i)._1 < thres) break;
        }
        return queryResult.subList(0, i);
    }
}
