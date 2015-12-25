package com.buzzit.indexer.cache;

import com.buzzit.TestQueries;
import com.buzzit.ranker.QueryResultItems;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 *
 */
public class QueryResultCacheTest {
    @Test
    public void testCache() {
        String queries[] = TestQueries.TEST_QUERIES;

        QueryResultCache cache = QueryResultCache.getInstance();

        for (int i = 0; i < queries.length; ++i) {
            cache.cacheQueryResult(queries[i], new QueryResultItems(queries[i]));
            assertNotEquals(null, cache.getQueryResult(queries[i]));
        }
    }
}