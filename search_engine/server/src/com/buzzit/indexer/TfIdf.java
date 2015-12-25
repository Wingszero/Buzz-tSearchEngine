package com.buzzit.indexer;

import com.buzzit.SearchEngineParam;
import com.buzzit.SparkConn;
import com.buzzit.indexer.stemmer.PorterStemmer;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.storage.StorageLevel;
import scala.Tuple2;

import java.io.Serializable;
import java.util.*;
/**
 *
 */
// TODO: hard code partition number may improve performance.
// TODO: longer posting length for champion list in lower hierarchy.
public class TfIdf {
    private static int QUERY_RESULT_LENGTH = SearchEngineParam.getQueryResultLength();
    private static int POSTING_LENGTH = SearchEngineParam.getPostingLength();
    private static int HIERARCHY_DEPTH = SearchEngineParam.getHierarchyDepth();
    /**
     * Save keyword into champion list only if that keyword has posting of length longer than threshold.
     */
    private static int CHAMPION_LIST_THRESHOLD = SearchEngineParam.getChampionListThreshold();

    private SparkConn conn;
    private JavaPairRDD<String, Iterable<Tuple2<String, Double>>> postings = null;
    private List<JavaPairRDD<String, Iterable<Tuple2<String, Double>>>> hierarchyList = null;
    //private JavaPairRDD<String, Iterable<Tuple2<String, Double>>> championList = null;

    public TfIdf() {
        init();
    }

    // PUBLIC
    /**
     * compute tfidf factor of a word-doc pair
     * @param dir
     * @return <<keyword, doc>, tfidf>
     */
    public void compute(String dir) {
        if (ConfigLoader.getRecompute()) {
            JavaPairRDD<String, String> input = getInput(dir);
            // <keyword, <doc, count>>
            JavaPairRDD<String, Tuple2<String, Integer>> wordCount = computeWordCount(input);
            JavaPairRDD<Tuple2<String, String>, Double> tfidfWeight = computeTfIdfWeight(wordCount,input);
            // option choice of replicating each partition to two cluster nodes.
            postings = computePostingsList(tfidfWeight);
            //postings.persist(StorageLevel.DISK_ONLY());
            //postings.count();

            // cache only the first hierarchy in memory only
            // others are cached in memory and disk.
            hierarchyList = computeHierarchyList(postings);
            saveToFile();
        } else {
            readFromFile();
        }
    }

    public void saveToFile() {
        try {
            //postings.saveAsObjectFile(ConfigLoader.getHDFS() +
//                    ConfigLoader.getTfidfPath() + "/postings");
            int level = 0;
            for (JavaPairRDD<String, Iterable<Tuple2<String, Double>>> levelList : hierarchyList) {
                levelList.saveAsObjectFile(ConfigLoader.getHDFS() +
                        ConfigLoader.getTfidfPath() + "/hierarchyList" + level);
                level++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void readFromFile() {
        try {
            //postings = JavaPairRDD.fromJavaRDD(conn.getSparkContext().objectFile(
            //        ConfigLoader.getHDFS() + ConfigLoader.getTfidfPath() + "/postings"
            //));
            //postings.persist(StorageLevel.DISK_ONLY());
            //postings.count();

            hierarchyList = new ArrayList<>();
            for (int i = 0; i < HIERARCHY_DEPTH; ++i) {
                JavaPairRDD<String, Iterable<Tuple2<String, Double>>> championList = JavaPairRDD
                        .fromJavaRDD(conn.getSparkContext()
                                .objectFile(ConfigLoader.getHDFS() +
                                                ConfigLoader.getTfidfPath() + "/hierarchyList" + i
                                ));
                if (i == 0) {
                    championList.persist(StorageLevel.MEMORY_AND_DISK());
                } else championList.persist(StorageLevel.DISK_ONLY());

                // can be blank only if there is no further levels.
                if (championList.count() > 0) {
                    hierarchyList.add(championList);
                } else break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<Tuple2<String, Double>> getQueryResult(String query) {
        List<Tuple2<String, Double>> docWeightList = computeDocWeight(query);

        // DEBUG
        // print top 10 results
        int count = 0;
        for (Tuple2<String, Double> pair : docWeightList) {
            System.out.println(count + " " + pair._2() + ": " + pair._1());
            count++;
            if (count >= QUERY_RESULT_LENGTH) break;
        }

        return docWeightList;
    }

    // PRIVATE

    private void init() {
        conn = SparkConn.getInstance();
    }

    /**
     * get the total number of input docs
     * @param input
     * @return total doc number
     */
    private long totalDoc(JavaPairRDD<String, String> input){
        long size = input.count();

        // debug
        System.out.println("Total files: " + size);

        return size;
    }

    /**
     * compute idf factor
     * @param wordCount <keyword,<doc, count>>
     * @param N total doc number
     * @return <keyword, idf>
     */
    private JavaPairRDD<String, Double> idfFactor(JavaPairRDD<String, Tuple2<String, Integer>> wordCount, long N){
        JavaPairRDD<String, Iterable<Tuple2<String, Integer>>> tf = wordCount
                .groupByKey();
        JavaPairRDD<String, Double> idf = tf.mapToPair(pair -> {
            Iterator<Tuple2<String, Integer>> iter = pair._2().iterator();
            int n = 0;
            while (iter.hasNext()) {
                iter.next();
                n++;
            }

            // debug
            //System.out.println("DEBUG: key: " + pair._1 + "idf: " + Math.log((double) N / (double) n));

            // smoothed IDF weight.
            return new Tuple2<>(pair._1(), 1 + Math.log((double) N / (double) n));
        });

        // debug
        //printIdf(idf);

        return idf;
    }

    /**
     * compute normalized tf factor from word count
     * @param wordCount <key,<doc, count>>
     * @return <<keyword, doc>, normalized_tf>
     */
    private JavaPairRDD<Tuple2<String, String>, Double> tfNormalize(JavaPairRDD<String, Tuple2<String, Integer>> wordCount){
        /*wordCount: <key, <doc, count>> -> wc1:<doc,<key, count>>*/
        JavaPairRDD<String, Tuple2<String, Integer>> wc1 = wordCount
                .mapToPair(
                        pair -> new Tuple2<>(pair._2()._1(), new Tuple2<>(pair._1(), pair._2()._2()))
                );

        /*get max tf: <doc, max_count>*/
        JavaPairRDD<String, Integer> max = wc1
                .reduceByKey((t1, t2) -> (t1._2() >= t2._2()) ? t1 : t2)
                .mapToPair(pair -> new Tuple2<>(pair._1(), pair._2()._2()));

        // wc1: <doc, <key, count>> -> <doc, <<key, count>, max_count>>
        JavaPairRDD<String, Tuple2<Tuple2<String, Integer>, Integer>> wc_max = wc1
                .join(max);

        // <doc, <<key, count>, max_count>> -> <<key, doc>, normalized_tf>
        JavaPairRDD<Tuple2<String, String>, Double> tf = wc_max
                .mapToPair(pair -> {
                    // values
                    String doc = pair._1();
                    String key = pair._2()._1()._1();
                    int count = pair._2()._1()._2();
                    int max_count = pair._2()._2();

                    double a = 0.4;
                    // max_count
                    int max_tf = max_count;
                    // a + (1 - a) * (count / max_count)
                    double factor = a + (1 - a) *
                            (Double.valueOf(count) / Double.valueOf(max_tf));
                    // <<key, doc>, normalized_tf>
                    return new Tuple2<>(new Tuple2<>(key, doc), factor);
                });

        // debug
        //printTf(tf);

        return tf;
    }

    // PROTECTED

    /**
     * compute tfidf weight factor of (keyword, doc) pair
     * we encode each doc as the format url::title
     * if the keyword is in title or url, we multiply its weight by pre-set parameter in SearchEngineParam.
     * @param wordCount
     * @param input
     * @return ((keyword,doc), tfidf)
     */
    protected JavaPairRDD<Tuple2<String, String>, Double>
    computeTfIdfWeight(JavaPairRDD<String, Tuple2<String, Integer>> wordCount,
                       JavaPairRDD<String, String> input){
        /*<<keyword, doc>, tf>*/
        JavaPairRDD<Tuple2<String,String>, Double> tf = tfNormalize(wordCount);
        long docCount = totalDoc(input);
        /*<keyword, idf>*/
        JavaPairRDD<String, Double> idf = idfFactor(wordCount, docCount);
        //<<keyword, doc>, tfidf>
        JavaPairRDD<Tuple2<String, String>, Double> weight = tf
                // <<keyword, doc>, tf> <keyword,<doc, tf>>
                .mapToPair(pair -> new Tuple2<>(pair._1()._1(), new Tuple2<>(pair._1()._2(), pair._2())))
                // <keyword, <doc, tf>> + <keyword, idf> -> <keyword,<<doc, tf>, idf>>
                .join(idf)
                // <keyword,<<doc, tf>, idf>> -> <<keyword, doc>, tfidf>
                .mapToPair(pair -> {
                    String keyword = pair._1;
                    String urlTitle = pair._2._1._1;
                    Double tfWeight = pair._2._1._2;
                    Double idfWeight = pair._2._2;

                    String urlTitlePair[] = urlTitle.split("::", 2);
                    String url = urlTitlePair[0];
                    List<String> titleStemmed = PorterStemmer.lemmatize(urlTitlePair[1]);

                    if (titleStemmed.contains(keyword)) {
                        tfWeight += SearchEngineParam.getTitleWeight();
                    } else if (url.contains(keyword)) {
                        tfWeight += SearchEngineParam.getUrlWeight();
                    }

                    return new Tuple2<>(new Tuple2<>(keyword, url), tfWeight * idfWeight);
                });

        return weight;
    }

    /**
     * Compute word count.
     * @param input <doc, content>
     * @return <keyword, <doc, count>>
     */
    protected JavaPairRDD<String, Tuple2<String, Integer>> computeWordCount(JavaPairRDD<String, String> input) {
        // <doc, content>
        JavaPairRDD<String, Tuple2<String, Integer>> wordCount = input
                // <doc, content> -> <doc, word>
                .flatMapToPair(pair -> {
                    String doc = pair._1();
                    List<Tuple2<String, String>> result = new ArrayList<>();
                    List<String> stemmedWords = PorterStemmer.lemmatize(pair._2());

                    for (int i=0; i<stemmedWords.size(); ++i) {
                        // single words
                        result.add(new Tuple2<>(doc, stemmedWords.get(i)));
                        // phrase
                        if (i+1 < stemmedWords.size()) {
                            result.add(new Tuple2<>(doc, stemmedWords.get(i) + " " + stemmedWords.get(i+1)));
                        }

                    }

                    return result;
                })
                // <doc, word> -> <<doc, word>, 1>
                .mapToPair(pair -> new Tuple2<>(new Tuple2<>(pair._1(), pair._2()), 1))
                // <<doc, word>, 1> -> <<doc, word>, count>
                .reduceByKey((a, b) -> a + b)
                // <<doc, word>, count> -> <word, <doc, count>>
                .mapToPair(pair -> new Tuple2<>(pair._1()._2(), new Tuple2<>(pair._1()._1(), pair._2())));
        return wordCount;
    }



    protected JavaPairRDD<String, String> getInput(String path) {
        return conn.getSparkContext()
                .textFile(path)
                .mapToPair(str -> {
                    try {
                        return new Tuple2<>(str.split("\t")[0], str.split("\t")[1]);
                    } catch (Exception e) {
                        e.printStackTrace();
                        System.out.println("Error str: " + str);
                        return new Tuple2<>(str.split("\t")[0], "");
                    }
                });
    }

    /**
     * Given query as String, compute weight for each document.
     * @param query
     * @return
     */
    protected List<Tuple2<String, Double>> computeDocWeight(String query) {
        // process query through stemming and stop words.
        List<String> queryList = PorterStemmer.parsePhraseQuery(query);

        System.out.println("Computing doc weight for query: ");

        List<Tuple2<String, Double>> docWeight = new ArrayList<>();

        int level = 0;
        for (JavaPairRDD<String, Iterable<Tuple2<String, Double>>> championList : hierarchyList) {
            List<Tuple2<String, Double>> newDocWeight = computeDocWeightFromList(queryList, championList,
                    QUERY_RESULT_LENGTH - docWeight.size());
            System.out.println("Level " + level + " champion list query result length: " + newDocWeight.size());
            level++;
            docWeight = combineAndSortQueryResult(docWeight, newDocWeight);

            if (docWeight.size() >= QUERY_RESULT_LENGTH) break;
        }

        return docWeight;
    }

    protected List<Tuple2<String, Double>>
    computeDocWeightFromList(List<String> queryList,
                             JavaPairRDD<String, Iterable<Tuple2<String, Double>>> postingList,
                             int topK) {
        List<Tuple2<String, Double>> docWeight = postingList
                //TODO further improve performance
                // get all <doc, tfidf> pair that has at least one term matches
                // <key, iter<doc, weight>> -> <doc, weight>
                .flatMapToPair(pair -> {
                    if (queryList.contains(pair._1())) {
                        return pair._2();
                    } else {
                        return new ArrayList<>();
                    }
                })
                .filter(pair -> pair._2() > 0)
                // get sum of all matched docs
                // reduce <doc, weight> by doc.
                .reduceByKey((a, b) -> a + b)
                .takeOrdered(topK, DocWeightComparator.INSTANCE);
        return docWeight;
    }

    /**
     * Sort doc weight.
     * @param docWeight <doc, weight>. ignore zero weight documents
     * @return <weight, doc>
     */
    protected JavaPairRDD<Double, String> sortDocWeight(JavaPairRDD<Double, String> docWeight) {
        return docWeight.sortByKey(false); // sort by weight in descending order.
    }

    // DEBUG

    protected JavaPairRDD<String, Iterable<Tuple2<String,Double>>>
    computePostingsList(JavaPairRDD<Tuple2<String, String>, Double> tfidf){
        JavaPairRDD<String, Iterable<Tuple2<String,Double>>> postings = tfidf
                //<<keyword, doc>, tfidf> -> <keyword,<doc,tfidf>>
                .mapToPair(pair -> new Tuple2<>(pair._1()._1(), new Tuple2<>(pair._1()._2(), pair._2())))
                //<keyword,<doc,tfidf>> -> <keyword, iterable<doc, tdidf>>
                .groupByKey();
        return postings;
    }

    /***
     * precompute for each keyword the r docs with highest tfidf factor in the postings,default r is 200
     * @param postings <<keyword, doc>, tfidf>
     * @return  <keyword,iterable<doc, top200tfidf>>
     */
    protected JavaPairRDD<String, Iterable<Tuple2<String, Double>>>
    computeChampionList(JavaPairRDD<String, Iterable<Tuple2<String, Double>>> postings, int topK) {
        JavaPairRDD<String, Iterable<Tuple2<String, Double>>> champion = postings
                .mapToPair(pair -> {
                    HeapSort heapSort = new HeapSort();
                    Iterable<Tuple2<String, Double>> iter = pair._2();
                    Iterable<Tuple2<String, Double>> top = heapSort.filter(iter, topK);
                    return new Tuple2<>(pair._1(), top);
                });
        return champion;
    }

    /**
     * Given posting list [keyword, Iterable[doc, weight]].
     * We wish to compute a hierarchy list of champion lists.
     * Each champion list is of the form [keyword, Iterable[doc, weight]], where the latter iterable is sorted and
     * is generally short (a length defined by POSTING_LENGTH).
     * The highest hierarchy has the largest weight.
     * Our approach is first use heap sort to obtain the POSTING_LENGTH * HIERARCHY_DEPTH number of Iterables for each
     * level of champion lists, and store them in a list. This intermediate structure will be
     * [keyword, Iterable[Iterable[doc, weight]]].
     * Then, we split the second term and form the list of hierarchy champion lists.
     * [keyword, Iterable[Iterable[doc, weight]]] -> List([keyword, Iterable[Iterable[doc, weight]]]).
     * @param postings
     * @return
     */
    protected List<JavaPairRDD<String, Iterable<Tuple2<String, Double>>>>
    computeHierarchyList(JavaPairRDD<String, Iterable<Tuple2<String, Double>>> postings) {
        List<JavaPairRDD<String, Iterable<Tuple2<String, Double>>>> hierarchyList = new ArrayList<>();
        JavaPairRDD<String, Iterable<Iterable<Tuple2<String, Double>>>> hierarchyIterable = postings
                // Now posting list of each level is seperated and stored in one iterable.
                .mapToPair(pair -> {
                    HeapSort heapSort = new HeapSort();
                    Iterable<Tuple2<String, Double>> iter = pair._2();
                    Iterable<Tuple2<String, Double>> top = heapSort.filter(iter, POSTING_LENGTH * HIERARCHY_DEPTH);
                    List<Tuple2<String, Double>> curTop = new ArrayList<>();
                    List<Iterable<Tuple2<String, Double>>> topList = new ArrayList<>();
                    int counter = 0;
                    int depth = 0;
                    for (Tuple2<String, Double> docWeightPair : top) {
                        counter++;
                        curTop.add(docWeightPair);
                        if (counter >= POSTING_LENGTH) {
                            topList.add(curTop);
                            curTop = new ArrayList<>();
                            counter = 0;
                            depth++;
                            if (depth >= HIERARCHY_DEPTH) break;
                        }
                    }
                    //??
                    if (curTop.size() > CHAMPION_LIST_THRESHOLD) {
                        // some keywords has shorter posting
                        // as a result finishes earlier.
                        topList.add(curTop);
                    }
                    return new Tuple2<>(pair._1(), topList);
                });

        // Split the posting list from the iterable level by level.
        for (int i=0; i<HIERARCHY_DEPTH; ++i) {
            int targetLevel = i;
            JavaPairRDD<String, Iterable<Tuple2<String, Double>>> championList = hierarchyIterable
                    .mapToPair(docIterableWeightPair -> {
                        Iterable<Iterable<Tuple2<String, Double>>> levelIter = docIterableWeightPair._2;
                        int level = 0;
                        for (Iterable<Tuple2<String, Double>> iter : levelIter) {
                            if (level == targetLevel) {
                                return new Tuple2<>(docIterableWeightPair._1, iter);
                            }
                            level++;
                        }
                        return null;
                    })
                    // filter keyword with no posting list left in this level.
                    .filter(pair -> pair != null);

            if (i == 0) {
                championList.persist(StorageLevel.MEMORY_AND_DISK());
            } else championList.persist(StorageLevel.DISK_ONLY());

            championList.count();

            hierarchyList.add(championList);
        }
        return hierarchyList;
    }

    protected List<Tuple2<String, Double>>
    combineAndSortQueryResult(List<Tuple2<String, Double>> docWeightA,
                              List<Tuple2<String, Double>> docWeightB) {
        HashSet<String> hitSet = new HashSet<>();
        System.out.println("Champion:");
        for (Tuple2<String, Double> pair : docWeightA) {
            System.out.println("c doc: " + pair._1() + " weight: " + pair._2());
            hitSet.add(pair._1());
        }
        System.out.println("Low:");
        for (Tuple2<String, Double> pair : docWeightB) {
            String doc = pair._1();
            if (!hitSet.contains(doc)) {
                docWeightA.add(pair);
                System.out.println("l doc: " + pair._1() + " weight: " + pair._2());
            }
        }
        docWeightA.sort(DocWeightComparator.INSTANCE);
        return docWeightA;
    }

    private static class DocWeightComparator implements Comparator<Tuple2<String, Double>>, Serializable {
        static DocWeightComparator INSTANCE = new DocWeightComparator();
        @Override
        public int compare(Tuple2<String, Double> o1, Tuple2<String, Double> o2) {
            return Double.compare(o2._2(), o1._2());
        }
    }

    private void printTf(JavaPairRDD<Tuple2<String, String>, Double> tf) {
        List<Tuple2<Tuple2<String, String>, Double>> output = tf.collect();
        for (Tuple2<Tuple2<String, String>, Double> tuple : output) {
            System.out.println("Key: " + tuple._1()._1() + ", doc: " + tuple._1()._2() + ", tf: " + tuple._2());
        }
    }

    private void printIdf(JavaPairRDD<String, Double> idf) {
        List<Tuple2<String, Double>> output = idf.collect();
        for (Tuple2<String, Double> tuple : output) {
            System.out.println("Key: " + tuple._1() + ", idf: " + tuple._2());
        }
    }
}
