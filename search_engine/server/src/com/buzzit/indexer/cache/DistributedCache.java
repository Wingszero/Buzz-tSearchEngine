package com.buzzit.indexer.cache;

import com.buzzit.SparkConn;
import com.buzzit.ranker.QueryResultItems;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import scala.Tuple2;

import java.util.ArrayList;
import java.util.List;

/**
 * Secondary level cache. We don't care consistency(multiple results with same query).
 * We don't care fairness(removing entry randomly/the first entry when filled).
 */
public class DistributedCache implements Cache {
    protected static int CACHE_LIMIT = 50000;
    protected static double CACHE_DUMP_VOL_FRACTION = 0.501;

    private SparkConn conn;
    private JavaPairRDD<String, QueryResultItems> cachedQueryItems;
    private long count = 0;

    public DistributedCache() {
        conn = SparkConn.getInstance();
        cachedQueryItems = conn.getSparkContext()
                .emptyRDD()
                .mapToPair(v -> new Tuple2<>("dummy", new QueryResultItems("dummy")));
    }

    @Override
    public boolean isQueryResultCached(String query) {
        if (cachedQueryItems != null) {
            return cachedQueryItems.lookup(query).size() > 0;
        } else return false;
    }

    @Override
    public QueryResultItems getQueryResult(String query) {
        List<QueryResultItems> queryResultItemsList = cachedQueryItems.lookup(query);
        if (queryResultItemsList.size() <= 0) return null;
        return queryResultItemsList.get(0);
    }

    @Override
    public QueryResultItems cacheQueryResult(String query, QueryResultItems items) {
        QueryResultItems swappedOutItems = cacheQueryResultRandom(query, items);
        return swappedOutItems;
    }

    @Override
    public int getCacheSize() {
        return (int)count;
    }

    @Override
    public void clear() {
        cachedQueryItems = cachedQueryItems.subtractByKey(cachedQueryItems);
        count = cachedQueryItems.count();
    }

    /** NUMEROUS CACHING SCHEMES **/

    /**
     * In our current implementation, once filled, we dump half of the cache randomly.
     * Thus we always return null in this method.
     * @param query
     * @param items
     * @return
     */
    protected QueryResultItems cacheQueryResultRandom(String query, QueryResultItems items) {
        if (count >= CACHE_LIMIT) {
            JavaPairRDD<String, QueryResultItems> toSubtract = cachedQueryItems.sample(false, CACHE_DUMP_VOL_FRACTION);
            // Strange bug, may due to RDD's lazy execution.
            //long toCount = toSubtract.count();
            //List<String> keys = cachedQueryItems.keys().collect();
            //keys = toSubtract.keys().collect();
            cachedQueryItems = cachedQueryItems.subtractByKey(toSubtract);
            count = cachedQueryItems.count();
            //keys = cachedQueryItems.keys().collect();
        }

        List<Tuple2<String, QueryResultItems>> newQueryItems = new ArrayList<>();
        newQueryItems.add(new Tuple2<>(query, items));
        cachedQueryItems = cachedQueryItems.union(conn.getSparkContext().parallelizePairs(newQueryItems));
        count++;

        return null;
    }
}
