package com.buzzit.indexer.db;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 *
 */
public class DBExtractorTest {
    @Test
    public void testDBExtractor() {
        try {
            DBExtractor extractor = new DBExtractor();
            extractor.extractDBToFile("../testdir/db_out");
        } catch (Exception e) {
            assertEquals("success", e.getMessage());
        }
    }
}