package com.buzzit.ranker;

import com.buzzit.RunAllTests;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 *
 */
public class RankerTest {
    static final String TEST_QUERY = "007";
    static final String TEST_QUERY1 = "software engineer";
    static final String TEST_QUERY2 = "James Bond";
    static final String TEST_QUERY3 = "2008–09 NCAA Division I men's basketball rankings";

    static final String TEST_QUERY_EXIST = "Phillip Preis";
    static final String TEST_QUERY_NOT_EXIST = "Starcraft";
    @Test
    public void testRanker() {
        try {
            RunAllTests.removeSavedFiles();

            Ranker ranker = new Ranker();

            ranker.query(TEST_QUERY);
            ranker.query(TEST_QUERY1);
            ranker.query(TEST_QUERY2);
            ranker.query(TEST_QUERY3);
            ranker.query(TEST_QUERY_EXIST);
            ranker.query(TEST_QUERY_NOT_EXIST);
        } catch (Exception e) {
            assertEquals("success", e.getMessage());
        }
    }
}