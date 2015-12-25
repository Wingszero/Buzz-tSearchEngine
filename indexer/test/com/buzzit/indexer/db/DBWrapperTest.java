package com.buzzit.indexer.db;

import com.myapp.worker.db.WebPageEntity;
import com.sleepycat.persist.PrimaryIndex;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 *
 */
public class DBWrapperTest {
    @Test
    public void testDBRead() {
        try {
            DBWrapper wrapper = new DBWrapper("../testdir/db");
            PrimaryIndex<String, WebPageEntity> idx = wrapper.getWebPageIdx();
        } catch (Exception e) {
            assertEquals("success", e.getMessage());
        }
    }
}