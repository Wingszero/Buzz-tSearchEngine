package com.buzzit;

import com.buzzit.imagesearch.ImageNet;
import com.buzzit.indexer.cache.QueryResultCache;
import com.buzzit.indexer.wiki.KnowledgeEntity;
import com.buzzit.indexer.wiki.KnowledgeGraph;
import com.buzzit.pagerank.PageRankRecords;
import com.buzzit.ranker.QueryPageEntity;
import com.buzzit.ranker.QueryPageEntityFactory;
import com.buzzit.ranker.QueryResultItems;
import com.buzzit.ranker.Ranker;

import java.io.File;
import java.util.List;
import java.util.Scanner;

/**
 *
 */
public class SearchEngine {
    private static SearchEngine se = null;
    private Ranker ranker;
    private KnowledgeGraph knowledgeGraph;
    private ImageNet imageNet;
    private QueryResultCache cache;

    public static SearchEngine getInstance() {
        if (se == null) {
            se = new SearchEngine();
        }
        return se;
    }

    private SearchEngine() {
        SearchEngineParam.init();

        ranker = new Ranker();

        knowledgeGraph = new KnowledgeGraph();
        knowledgeGraph.computeKnowledgeGraph();

        imageNet = new ImageNet();

        cache = QueryResultCache.getInstance();
    }

    public QueryResultItems query(String query) {
        QueryResultItems resultItems;
        // check cache
        if ((resultItems = cache.getQueryResult(query)) != null) {
            return resultItems;
        }

        // query from ranker
        List<String> queryResultUrlList = ranker.query(query);

        // query from knowledge graph
        KnowledgeEntity knowledgeEntity = knowledgeGraph.getQueryResult(query);

        // construct query result items
        resultItems = getQueryResultItems(queryResultUrlList, knowledgeEntity, query);

        // update cache
        cache.cacheQueryResult(query, resultItems);

        return resultItems;
    }

    public QueryResultItems queryImage(File img) {
        String queryStr = imageNet.getImageClassification(img);

        return query(queryStr);
    }

    protected QueryResultItems getQueryResultItems(List<String> queryResultUrlList,
                                                   KnowledgeEntity knowledgeEntity,
                                                   String query) {
        QueryResultItems resultItems = new QueryResultItems(query);

        resultItems.addKnowledgeEntity(knowledgeEntity);

        List<QueryPageEntity> pageEntities = QueryPageEntityFactory.constructPageEntities(queryResultUrlList, query);
        pageEntities.forEach(entity -> resultItems.addPageEntity(entity));

        return resultItems;
    }

    public static void main(String args[]) {
        SearchEngine se = new SearchEngine();
        long queryTime;
        int k = 10;
        String query;
        Scanner reader = new Scanner(System.in);
        QueryResultItems queryResultItems;
        KnowledgeEntity entity;
        while (true) {
            System.out.print("Please input your query: ");
            query = reader.nextLine();

            queryTime = System.currentTimeMillis();
            queryResultItems = se.query(query);
            queryTime = System.currentTimeMillis() - queryTime;

            entity = queryResultItems.getKnowledgeEntity();

            System.out.println("Search: " + query + " took " + (double)queryTime/1000 + "s.");
            int counter = 0;
            if (queryResultItems != null) {
                List<QueryPageEntity> pageList = queryResultItems.getWebPageEntities();
                for (QueryPageEntity page : pageList) {
                    System.out.println((counter+1) + "." + page.getTitle());
                    System.out.println(page.getUrl());
                    System.out.println(page.getAbs());
                    counter++;
                    if (counter >= k) break;
                }
            }
            if (counter == 0) {
                System.out.println("Query Not found");
            }

            System.out.println("");
            if (entity != null) {
                System.out.println("Title: " + entity.getTitle());
                System.out.println("Url: " + entity.getUrl());
                System.out.println("Content: " + entity.getContent());
            } else System.out.println("Wiki Not found");
        }
    }
}
