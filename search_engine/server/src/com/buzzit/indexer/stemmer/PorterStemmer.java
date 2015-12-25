package com.buzzit.indexer.stemmer;

import java.util.*;
/**
 *
 */
public class PorterStemmer {
    private static PorterStemmer porterStemmer = null;
    private Stemmer stemmer;
    private StopList stop;

    private PorterStemmer() {
        stemmer = new Stemmer();
        stop = new StopList();
    }

    private static void init() {
        if (porterStemmer == null) {
            porterStemmer = new PorterStemmer();
        }
        porterStemmer.stop.populateStopList();
    }

    public static List<String> parseQuery(String query) {
        return PorterStemmer.lemmatize(query);
    }

    public static List<String> parsePhraseQuery(String query) {
        List<String> queryList = PorterStemmer.lemmatize(query);
        int size = queryList.size();

        // 2 phrase
        for (int i=0; i<size-1; ++i) {
            queryList.add(queryList.get(i) + " " + queryList.get(i+1));
        }

        return queryList;
    }

    public static List<String> lemmatize(String docText) {
        return lemmatize(docText, true);
    }

    public static List<String> lemmatize(String docText, boolean stopWords) {
        init();
        String[] words = docText.toLowerCase().split("\\s+");
        List<String> stemmedWords = new ArrayList<>();
        for (String word : words) {
            if(stopWords && StopList.stopList.contains(word)) continue;
            try {
                porterStemmer.stemmer.add(word.toCharArray(), word.length());
                porterStemmer.stemmer.stem();
                String stemmed = porterStemmer.stemmer.toString();
                stemmedWords.add(stemmed);
            } catch (Exception e) {
                porterStemmer.stemmer = new Stemmer();
                stemmedWords.add(word);
                e.printStackTrace();
                System.out.println("Error stemming: " + word);
            }
        }
        return stemmedWords;
    }
}
