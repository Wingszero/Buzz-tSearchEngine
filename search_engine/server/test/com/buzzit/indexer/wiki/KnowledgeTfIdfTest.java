package com.buzzit.indexer.wiki;

import com.buzzit.RunAllTests;
import org.junit.Test;

/**
 *
 */
public class KnowledgeTfIdfTest {
    private static final String TEST_DIR = "../testdir/wiki";
    private static final String TEST_QUERY_EXIST = "Phillip Preis";
    private static final String TEST_QUERY_NOT_EXIST = "Starcraft";

    @Test
    public void testKnowTfIdf() {
        RunAllTests.removeSavedFiles();

        KnowledgeTfIdf tfIdf = new KnowledgeTfIdf();
        tfIdf.compute(TEST_DIR);
        tfIdf.getQueryResult(TEST_QUERY_EXIST);
        tfIdf.getQueryResult(TEST_QUERY_NOT_EXIST);
    }
}