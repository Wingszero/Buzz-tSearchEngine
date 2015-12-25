package com.buzzit.indexer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
/**
 *
 */
public class ConfigLoader {
    //private static final String MASTER_HOSTIP = "";

    // local mode
    //public static final String SPARK_MASTER = "local"; // local mode
    //public static final String HDFS = ""; // Local mode

    // distributed mode
//    public static final String SPARK_MASTER = "spark://" +
//            MASTER_HOSTIP +
//            "compute-1.amazonaws.com:7077"; // distributed mode
//    public static final String HDFS = "hdfs://" +
//            MASTER_HOSTIP +
//            "compute-1.amazonaws.com:9000"; // distributed mode

    private static String SPARK_MASTER;
    private static String HDFS;
    private static HashMap<String, String> SPARK_SETTING;

    private static String INDEXER_CORPUS_PATH; // = "/testdir/corpus";
    private static String KNOW_GRAPH_CORPUS_PATH; // = "/testdir/wiki";

    private static String TFIDF_PATH; // = "/testdir/tfidf";
    private static String WIKI_TFIDF_PATH; // = "/testdir/wiki_tfidf"

    private static boolean RECOMPUTE;

    private static ConfigLoader configLoader = null;
    private ConfigLoader() {
        try {
            File confFile = new File("spark.conf");
            System.out.println("Reading URL from file...");
            BufferedReader in = new BufferedReader(new FileReader(confFile));

            SPARK_MASTER = in.readLine();
            System.out.println("Spark master: " + SPARK_MASTER);

            HDFS = in.readLine();
            System.out.println("HDFS: " + HDFS);

            INDEXER_CORPUS_PATH = in.readLine();
            System.out.println("indexer corpus: " + INDEXER_CORPUS_PATH);

            KNOW_GRAPH_CORPUS_PATH = in.readLine();
            System.out.println("Wiki: " + KNOW_GRAPH_CORPUS_PATH);

            TFIDF_PATH = in.readLine();
            System.out.println("tfidf: " + TFIDF_PATH);

            WIKI_TFIDF_PATH = in.readLine();
            System.out.println("Wiki tfidf: " + WIKI_TFIDF_PATH);

            String recompute = in.readLine();
            if (recompute.equals("recompute")) {
                RECOMPUTE = true;
            } else RECOMPUTE = false;

            String line;
            SPARK_SETTING = new HashMap<>();
            while ((line = in.readLine()) != null) {
                String pair[] = line.split("\\s+");
                SPARK_SETTING.put(pair[0], pair[1]);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void init() {
        if (configLoader == null) {
            configLoader = new ConfigLoader();
        }
    }

    public static String getSparkMaster() {
        init();
        return SPARK_MASTER;
    }

    public static String getHDFS() {
        init();
        return HDFS;
    }

    public static String getCorpusPath() {
        init();
        return INDEXER_CORPUS_PATH;
    }

    public static String getWikiPath() {
        init();
        return KNOW_GRAPH_CORPUS_PATH;
    }

    public static String getTfidfPath() {
        init();
        return TFIDF_PATH;
    }

    public static String getWikiTfidfPath() {
        init();
        return WIKI_TFIDF_PATH;
    }

    public static boolean getRecompute() {
        init();
        return RECOMPUTE;
    }

    public static HashMap<String, String> getSparkSetting() {
        init();
        return SPARK_SETTING;
    }

}
