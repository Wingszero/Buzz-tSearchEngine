package com.buzzit.indexer.cache;

import com.buzzit.ranker.QueryResultItems;

/**
 *
 */
public interface Cache {
    public boolean isQueryResultCached(String query);
    public QueryResultItems getQueryResult(String query);
    public QueryResultItems cacheQueryResult(String query, QueryResultItems items);

    public int getCacheSize();
    public void clear();
}
