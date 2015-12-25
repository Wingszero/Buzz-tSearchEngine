package com.buzzit.indexer.cache;

import com.buzzit.TestQueries;
import com.buzzit.ranker.QueryResultItems;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 *
 */
public class CentralCacheTest {
    @Test
    public void testCentralCache() {
        CentralCache cache = new CentralCache();
        cache.CACHE_LIMIT = 2;

        String queries[] = TestQueries.TEST_QUERIES;

        // Test isQueryResultCached
        assertEquals(false, cache.isQueryResultCached(queries[0]));
        cache.cacheQueryResult(queries[0], new QueryResultItems(queries[0]));
        assertEquals(1, cache.getCacheSize());

        // Test cacheQueryResult
        cache.cacheQueryResult(queries[1], new QueryResultItems(queries[1]));
        assertNotEquals(null, cache.getQueryResult(queries[1]));
        assertEquals(2, cache.getCacheSize());

        // Test replace cache result based on usage
        // query 0: 1, query 1: 2
        assertEquals(queries[0],
                cache.cacheQueryResult(queries[2], new QueryResultItems(queries[2])).getQuery());
    }
}