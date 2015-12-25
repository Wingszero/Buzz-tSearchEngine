package com.buzzit;

import com.buzzit.indexer.ConfigLoader;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;

import java.util.*;

/**
 *
 */
public class SparkConn {
    private static SparkConn conn = null;
    private SparkConf conf;
    private JavaSparkContext sc;

    private SparkConn() {
        init();
    }

    private void init() {
        conf = new SparkConf().setAppName("Search Engine").setMaster(ConfigLoader.getSparkMaster());
        HashMap<String, String> setting = ConfigLoader.getSparkSetting();
        for (Map.Entry<String, String> pair : setting.entrySet()) {
            System.out.println("Spark setting: " + pair.getKey() + " - " + pair.getValue());
            conf.set(pair.getKey(), pair.getValue());
        }
        sc = new JavaSparkContext(conf);
        sc.addJar("search-engine.jar");
    }

    public static SparkConn getInstance() {
        if (conn == null) {
            conn = new SparkConn();
        }
        return conn;
    }

    public JavaSparkContext getSparkContext() {
        SparkConn.getInstance();
        return conn.sc;
    }
}
