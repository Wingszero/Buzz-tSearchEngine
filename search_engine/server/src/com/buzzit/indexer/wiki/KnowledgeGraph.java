package com.buzzit.indexer.wiki;

import com.buzzit.indexer.ConfigLoader;
import scala.Tuple2;

import java.util.List;

/**
 * Return entity lookup based on wiki corpus.
 */
public class KnowledgeGraph {
    private static final double WEIGHT_THRESHOLD = 0;
    private KnowledgeTfIdf tfIdf;

    public KnowledgeGraph() {
    }

    public void computeKnowledgeGraph() {
        String wikiPath = ConfigLoader.getHDFS() + ConfigLoader.getWikiPath();

        tfIdf = new KnowledgeTfIdf();
        System.out.println("Read from: " + wikiPath);
        tfIdf.compute(wikiPath);
    }

    public KnowledgeEntity getQueryResult(String query) {
        List<Tuple2<String, Double>> resultList = tfIdf.getQueryResult(query);

        if (resultList.size() == 0 || resultList.get(0)._2 < WEIGHT_THRESHOLD) {
            return null;
        }

        int counter = 0;
        System.out.println("Wiki result: " + query);
        for (Tuple2<String, Double> pair : resultList) {
            System.out.println((counter+1) + ". " + pair._2 + " " + pair._1);
        }

        // doc_id encoded as url::title::abstract.
        String urlTitleAbstract = resultList.get(0)._1;
        String tuple[] = urlTitleAbstract.split("::");
        return new KnowledgeEntity(tuple[1], tuple[2], tuple[0]);
    }
}
