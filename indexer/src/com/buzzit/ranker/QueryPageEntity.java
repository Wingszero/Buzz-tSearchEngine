package com.buzzit.ranker;

import com.buzzit.indexer.stemmer.PorterStemmer;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

/**
 *
 */
public class QueryPageEntity implements Serializable {
    private static final int COVER_NUM = 5;

    private String title;
    private String url;
    private String abs;

    public QueryPageEntity(String title, String url, String abs) {
        this.title = title;
        this.url = url;
        this.abs = abs;
    }

    public QueryPageEntity(String title, String url, String content, String query) {
        this.title = title;
        this.url = url;
        this.abs = parseAbstract(content, query);
    }

    public String getTitle() {
        return title;
    }

    public String getUrl() {
        return url;
    }

    public String getAbs() {
        return abs;
    }

    private String parseAbstract(String content, String query) {
        // must assure index for each stemmed word is the same with unstemmed.
        // that is, don't remove word according to stoplist.
        List<String> contentStemmedWords = PorterStemmer.lemmatize(content, false);
        List<String> contentWords = Arrays.asList(content.split("\\s+"));
        List<String> queryWords = PorterStemmer.lemmatize(query);
        HashSet<String> queryWordSet = new HashSet<>();
        queryWords.forEach(word -> queryWordSet.add(word));

        // Find occurance
        List<Integer> queryOccurances = new ArrayList<>();
        for (int i=0; i<contentStemmedWords.size(); ++i) {
            // change matched word color.
            if (queryWordSet.contains(contentStemmedWords.get(i))) {
                contentWords.set(i, "<em>" + contentWords.get(i) + "<em>");
                queryOccurances.add(i);
            }
        }

        // form abstract.
        // expand each keyword occurances to COVER_NUM neighbors.
        // TODO: abstract prune scheme:
        // 1. each query word appearing only once
        // 2. max abstract length threshold
        int currentLastIndex = 0;
        StringBuilder abstractBuilder = new StringBuilder();
        for (int matchIndex : queryOccurances) {
            // word and its neighbor already covered
            if (matchIndex + COVER_NUM <= currentLastIndex) continue;
            else {
                // word and its neighbor are words away from current last covered
                if (matchIndex - COVER_NUM > currentLastIndex) {
                    abstractBuilder.append("... ");
                }
                // append word from max(curLastIndex+1, matchIndex-COVER_NUM) to min(matchIndex+COVER_NUM, size-1)
                int start = matchIndex - COVER_NUM;
                if (start <= currentLastIndex) start = currentLastIndex + 1;
                int end = matchIndex + COVER_NUM;
                if (end >= contentWords.size()) end = contentWords.size() - 1;
                for (int i=start; i<=end; ++i) {
                    abstractBuilder.append(contentWords.get(i));
                    abstractBuilder.append(" ");
                }
                currentLastIndex = matchIndex + COVER_NUM;
            }

            if (abstractBuilder.length() > 20) break;
        }
        return abstractBuilder.toString();
    }
}
