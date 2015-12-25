package edu.upenn.cis455;

import com.google.common.collect.Iterables;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.PairFlatMapFunction;
import org.apache.spark.api.java.function.PairFunction;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;
import java.util.regex.Pattern;

import scala.Tuple2;

/**
 * The PageRank module takes in URL mappings and computes the page rank
 * for each of the input URLs.
 */
public final class PageRank {
  private static int NUM_OF_ITERATIONS = 10;
  
  private static class Sum implements Function2<Double, Double, Double> {
    @Override
    public Double call(Double a, Double b) {
      return a + b;
    }
  }
  
  public static void main(String[] args) throws Exception {
    if (args.length < 1) {
      System.err.println("Usage: PageRank <filename> [num of iterations]");
      System.exit(1);
    }
    
    if (args.length >= 2) {
      NUM_OF_ITERATIONS = Integer.parseInt(args[1]);
    }
    // Configure spark.
    SparkConf sparkConf = new SparkConf().setAppName("PageRank");
    JavaSparkContext javaSparkContext = new JavaSparkContext(sparkConf);
    
    JavaPairRDD<String, Iterable<String>> links = loadUrlsAndInitNeighbors(javaSparkContext, args[0] /*filename*/);
    JavaPairRDD<String, Double> ranks = runPageRankOnLinks(links, NUM_OF_ITERATIONS);
    dumpOutput(ranks);
    javaSparkContext.stop();
  }
  
  /**
   * Input should have one pair per line, where each line represents
   * a link from url A to url B, where A and B are separated by tab.
   */
  private static JavaPairRDD<String, Iterable<String>> loadUrlsAndInitNeighbors(JavaSparkContext javaSparkContext, String filename) {
    JavaRDD<String> lines = javaSparkContext.textFile(filename, 1);
    return lines.mapToPair(new PairFunction<String, String, String>() {
      @Override
      public Tuple2<String, String> call(String s) {
        String[] parts = s.split("\t");
        if (parts.length >= 2) {
          return new Tuple2<String, String>(parts[0], parts[1]);
        } else {
          return new Tuple2<String, String>("", "");
        }
      }
    }).distinct().groupByKey().cache();
  }
  
  /**
   * Recursively apply the PageRank algorithm.
   */
  private static JavaPairRDD<String, Double> runPageRankOnLinks(JavaPairRDD<String, Iterable<String>> links, int numOfIterations) {
    // Initialize all ranks to 1.
    JavaPairRDD<String, Double> ranks = links.mapValues(
      new Function<Iterable<String>, Double>() {
        @Override
        public Double call(Iterable<String> rs) {
          return 1.0;
        }
     });
    
    // Calculates and updates URL ranks continuously using PageRank algorithm.
    for (int current = 0; current < numOfIterations; current++) {
      // Calculates URL contributions to the rank of other URLs.
      JavaPairRDD<String, Double> contribs = links.join(ranks).values()
      .flatMapToPair(new PairFlatMapFunction<Tuple2<Iterable<String>, Double>, String, Double>() {
        @Override
        public Iterable<Tuple2<String, Double>> call(Tuple2<Iterable<String>, Double> s) {
          int urlCount = Iterables.size(s._1);
          List<Tuple2<String, Double>> results = new ArrayList<Tuple2<String, Double>>();
          for (String n : s._1) {
            results.add(new Tuple2<String, Double>(n, s._2() / urlCount));
          }
          return results;
        }
      });
      
      // Re-calculates URL ranks based on neighbor contributions. Added decay factor to
      // deal with sinks.
      ranks = contribs.reduceByKey(new Sum()).mapValues(new Function<Double, Double>() {
        @Override
        public Double call(Double sum) {
          return 0.15 + sum * 0.85; // alpha + sum * (1 - alpha), alpha value as suggested by Page and Brin
        }
      });
    }
    return ranks;
  }
  
  private static void dumpOutput(JavaPairRDD<String, Double> ranks) {
    // Dump to console.
    List<Tuple2<String, Double>> output = ranks.collect();
    for (Tuple2<?,?> tuple : output) {
      println(tuple._1() + "\t" + tuple._2());
    }
  }
  
  private static void println(String s) {
    System.out.println(s);
  }
}
