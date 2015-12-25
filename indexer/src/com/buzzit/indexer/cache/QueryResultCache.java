package com.buzzit.indexer.cache;

import com.buzzit.ranker.QueryResultItems;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class QueryResultCache {
    private static QueryResultCache queryResultCache = null;

    private List<Cache> cacheHierarchy;

    private QueryResultCache() {
        cacheHierarchy = new ArrayList<>();
        cacheHierarchy.add(new CentralCache());
        cacheHierarchy.add(new DistributedCache());
    }

    public static QueryResultCache getInstance() {
        if (queryResultCache == null) {
            queryResultCache = new QueryResultCache();
        }
        return queryResultCache;
    }

    public QueryResultItems getQueryResult(String query) {
        QueryResultItems resultItems = null;

        for (Cache cache : cacheHierarchy) {
            resultItems = cache.getQueryResult(query);
            if (resultItems != null) return resultItems;
        }

        return null;
    }

    public void cacheQueryResult(String query, QueryResultItems newItems) {
        QueryResultItems swappedItem = null;

        for (Cache cache : cacheHierarchy) {
            swappedItem = cache.cacheQueryResult(query, newItems);
            if (swappedItem != null) {
                query = swappedItem.getQuery();
                newItems = swappedItem;
            } else return;
        }
    }
}
