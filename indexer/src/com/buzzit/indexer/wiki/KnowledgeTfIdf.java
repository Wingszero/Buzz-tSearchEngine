package com.buzzit.indexer.wiki;

import com.buzzit.indexer.ConfigLoader;
import com.buzzit.indexer.stemmer.PorterStemmer;
import com.buzzit.SparkConn;
import com.buzzit.indexer.TfIdf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.storage.StorageLevel;
import scala.Tuple2;

import java.io.Serializable;
import java.util.*;
/**
 *
 */
public class KnowledgeTfIdf extends TfIdf {
    private SparkConn conn;
    private JavaPairRDD<String, Iterable<String>> postings = null;

    public KnowledgeTfIdf() {
        init();
    }

    @Override
    public void compute(String dir) {
        if (ConfigLoader.getRecompute()) {
            // <doc, content>
            JavaPairRDD<String, String> input = getInput(dir);
            postings = input
                    // <doc, keyword>
                    .flatMapToPair(pair -> {
                        List<Tuple2<String, String>> result = new ArrayList<>();
                        PorterStemmer.lemmatize(pair._2())
                                .forEach(word -> result.add(new Tuple2<>(pair._1(), word)));
                        return result;
                    })
                    // <doc, keyword> remove duplicate
                    .distinct()
                    // <doc, iterable<keyword>>
                    .groupByKey();
            postings.persist(StorageLevel.MEMORY_AND_DISK());
            postings.count();
            saveToFile();
        } else {
            readFromFile();
        }
    }

    @Override
    public List<Tuple2<String, Double>> getQueryResult(String query){
        List<String> queryList = PorterStemmer.parseQuery(query);
        int querySize = queryList.size();
        // <doc, iterable<keyword>>
        List<Tuple2<String, Tuple2<Integer, Integer>>> resultItemList = postings
                // <doc, <matchSize, docSize>>
                .mapToPair(pair -> {
                    Iterable<String> keywords = pair._2;
                    String doc = pair._1;
                    int matchSize = 0;
                    int docSize = 0;
                    for (String word : keywords) {
                        if (queryList.contains(word)) {
                            matchSize++;
                        }
                        docSize++;
                    }
                    return new Tuple2<>(doc, new Tuple2<>(matchSize, docSize));
                })
                // filter with doc contains query
                .filter(pair -> pair._2._1 == querySize)
                // sort with acsending order in docSize
                .takeOrdered(1, DocDSizeMSizeComparator.INSTANE);
        Tuple2<String, Tuple2<Integer, Integer>> resultItem = null;
        if (resultItemList.size() > 0) {
            resultItem = resultItemList.get(0);
        }
        List<Tuple2<String, Double>> queryResultList = new ArrayList<>();
        if (resultItem != null) {
            String doc = resultItem._1;
            int docSize = resultItem._2()._2();
            queryResultList.add(new Tuple2<>(doc, (double)docSize));
        }
        return queryResultList;
    }

    @Override
    public void saveToFile() {
        postings.saveAsObjectFile(ConfigLoader.getHDFS() +
                ConfigLoader.getWikiTfidfPath() + "/postings");
    }

    @Override
    public void readFromFile() {
        postings = JavaPairRDD.fromJavaRDD(conn.getSparkContext().objectFile(
                ConfigLoader.getHDFS() + ConfigLoader.getWikiTfidfPath() + "/postings"
        ));
        postings.persist(StorageLevel.MEMORY_AND_DISK());
        postings.count();
    }

    private void init() {
        conn = SparkConn.getInstance();
    }

    private static class DocDSizeMSizeComparator
            implements Comparator<Tuple2<String, Tuple2<Integer, Integer>>>, Serializable {
        static DocDSizeMSizeComparator INSTANE = new DocDSizeMSizeComparator();
        @Override
        public int compare(Tuple2<String, Tuple2<Integer, Integer>> o1,
                           Tuple2<String, Tuple2<Integer, Integer>> o2) {
            // <doc, <matchSize, docSize>>
            // return acsending order in docSize.
            return Integer.compare(o1._2()._2(), o2._2()._2());
        }
    }

}
