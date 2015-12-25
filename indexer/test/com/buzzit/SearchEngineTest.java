package com.buzzit;

import com.buzzit.indexer.cache.CentralCache;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 *
 */
public class SearchEngineTest {
    @Test
    public void testQuery() {
        RunAllTests.removeSavedFiles();

        SearchEngine searchEngine = new SearchEngine();
        long time = System.currentTimeMillis();
        String queries[] = TestQueries.TEST_QUERIES;
        for (String query : queries) {
            searchEngine.query(query);
        }

        queries = TestQueries.TEST_QUERIES_EXIST;
        for (String query : queries) {
            searchEngine.query(query);
        }

        queries = TestQueries.TEST_QUERIES_NOT_EXIST;
        for (String query : queries) {
            searchEngine.query(query);
        }
        time = System.currentTimeMillis() - time;
        System.out.println("No hit: " + time / 1000 + "s. ");

        time = System.currentTimeMillis();
        queries = TestQueries.TEST_QUERIES;
        for (String query : queries) {
            searchEngine.query(query);
        }

        queries = TestQueries.TEST_QUERIES_EXIST;
        for (String query : queries) {
            searchEngine.query(query);
        }

        queries = TestQueries.TEST_QUERIES_NOT_EXIST;
        for (String query : queries) {
            searchEngine.query(query);
        }
        time = System.currentTimeMillis() - time;
        System.out.println("With hit: " + time / 1000 + "s. ");
    }
}