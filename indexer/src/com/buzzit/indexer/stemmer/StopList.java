package com.buzzit.indexer.stemmer;

import java.util.*;

/**
 * Created by linwei on 12/5/15.
 */
public class StopList {
    public static final Set<String> stopList = new HashSet<>();
    private static final String[] stopwords= {"a","an","and","are","as","at","be","by","for","from",
            "has","he","in","is","it","its","of","on","that","the","to","was","were","will","with"};

    public StopList(){}

    public static void populateStopList(){
        if(isUpdated() == false) {
            for (String word : stopwords) {
                stopList.add(word);
            }
        }
    }
    private static boolean isUpdated(){
        return stopList.size()==stopwords.length;
    }
}
