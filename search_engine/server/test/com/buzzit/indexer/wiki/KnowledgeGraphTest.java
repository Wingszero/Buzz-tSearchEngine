package com.buzzit.indexer.wiki;

import com.buzzit.RunAllTests;
import com.buzzit.indexer.wiki.KnowledgeEntity;
import com.buzzit.indexer.wiki.KnowledgeGraph;
import org.junit.Test;
import static org.junit.Assert.*;
/**
 *
 */
public class KnowledgeGraphTest {
    private static final String TEST_QUERY = "software engineer";
    private static final String TEST_QUERY1 = "James Bond";
    private static final String TEST_QUERY2 = "2008–09 NCAA Division I men's basketball rankings";

    private static final String TEST_QUERY_EXIST = "Phillip Preis";
    private static final String TEST_QUERY_NOT_EXIST = "Starcraft";

    @Test
    public void testKnowledgeGraph() {
        RunAllTests.removeSavedFiles();

        KnowledgeGraph knowledgeGraph = new KnowledgeGraph();
        knowledgeGraph.computeKnowledgeGraph();
        knowledgeGraph.getQueryResult(TEST_QUERY);
        knowledgeGraph.getQueryResult(TEST_QUERY1);

        KnowledgeEntity entity = knowledgeGraph.getQueryResult(TEST_QUERY2);
        assertEquals("https://en.wikipedia.org/wiki/2008%E2%80%9309_NCAA_Division_I_men%27s_basketball_rankings",
                entity.getUrl());
        assertEquals(TEST_QUERY2, entity.getTitle());
        assertEquals("Two human polls made up the 2008–09 NCAA Division I men's basketball" +
                        " rankings, the AP Poll and the Coaches Poll, " +
                        "in addition to various publications' preseason polls.",
                entity.getContent());
    }

    @Test
    public void testKnowledgeGraphMatchAccuracy() {
        RunAllTests.removeSavedFiles();

        KnowledgeGraph knowledgeGraph = new KnowledgeGraph();
        knowledgeGraph.computeKnowledgeGraph();
        KnowledgeEntity entity;

        entity = knowledgeGraph.getQueryResult(TEST_QUERY_EXIST);
        assertEquals(TEST_QUERY_EXIST, entity.getTitle());

        entity = knowledgeGraph.getQueryResult(TEST_QUERY_NOT_EXIST);
        assertEquals(null, entity);
    }
}