package com.buzzit.indexer;

import com.buzzit.RunAllTests;
import org.junit.Test;

/**
 *
 */
public class IndexerTest {
    private static final String TEST_QUERY = "software engineer";

    @Test
    public void testIndexer() {
        RunAllTests.removeSavedFiles();

        Indexer indexer = new Indexer();
        indexer.computeIndexer();
        indexer.getQueryResult(TEST_QUERY);
        indexer.getQueryResult(TEST_QUERY, 0, 9);
        indexer.getQueryResult(TEST_QUERY, 1.5);
    }
}