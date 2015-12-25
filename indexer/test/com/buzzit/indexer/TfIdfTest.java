package com.buzzit.indexer;


import com.buzzit.RunAllTests;
import org.junit.Test;
/**
 *
 */
public class TfIdfTest {
    private static final String TEST_DIR = "../testdir/small";
    private static final String TEST_QUERY = "See above.";

    @Test
    public void testTfIdf() {
        RunAllTests.removeSavedFiles();

        TfIdf tfIdf = new TfIdf();
        tfIdf.compute(TEST_DIR);
        tfIdf.getQueryResult(TEST_QUERY);
    }
}