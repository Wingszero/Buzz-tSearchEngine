package com.buzzit.indexer.cache;

import com.buzzit.ranker.QueryResultItems;

import java.util.*;

/**
 *
 */
public class CentralCache implements Cache {
    protected static int CACHE_LIMIT = 500;
    private HashMap<String, QueryResultItems> cachedQueryItems;
    private HashMap<String, Integer> cachedQueryVisitCount;

    public CentralCache() {
        cachedQueryItems = new HashMap<>();
        cachedQueryVisitCount = new HashMap<>();
    }

    @Override
    public boolean isQueryResultCached(String query) {
        return cachedQueryItems.containsKey(query);
    }

    @Override
    public QueryResultItems getQueryResult(String query) {
        if (!cachedQueryItems.containsKey(query)) return null;
        int visitCount = cachedQueryVisitCount.get(query);
        cachedQueryVisitCount.put(query, visitCount + 1);
        return cachedQueryItems.get(query);
    }

    @Override
    public QueryResultItems cacheQueryResult(String query, QueryResultItems items) {
        QueryResultItems swappedOutItems = cacheQueryResultLeastVisit(query, items);
        return swappedOutItems;
    }

    @Override
    public int getCacheSize() {
        return cachedQueryItems.size();
    }

    @Override
    public void clear() {
        cachedQueryItems.clear();
        cachedQueryVisitCount.clear();
    }

    /** NUMEROUS CACHING SCHEMES **/

    protected QueryResultItems cacheQueryResultLeastVisit(String query, QueryResultItems items) {
        QueryResultItems swappedOutItems = null;
        if (cachedQueryItems.size() >= CACHE_LIMIT && !cachedQueryItems.containsKey(query)) {
            String leastVisitQuery = null;
            int leastVisitCount = Integer.MAX_VALUE;
            for (Map.Entry<String, Integer> queryCountPair : cachedQueryVisitCount.entrySet()) {
                if (queryCountPair.getValue() < leastVisitCount) {
                    leastVisitQuery = queryCountPair.getKey();
                }
            }
            swappedOutItems = cachedQueryItems.get(leastVisitQuery);
            cachedQueryItems.remove(leastVisitQuery);
            cachedQueryVisitCount.remove(leastVisitQuery);
        }
        cachedQueryItems.put(query, items);
        cachedQueryVisitCount.put(query, 1);
        return swappedOutItems;
    }
}
