package com.buzzit.indexer.stemmer;

import edu.stanford.nlp.ling.*;
import edu.stanford.nlp.pipeline.*;
import edu.stanford.nlp.util.*;

import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

/**
 * Deprecated as of low efficiency.
 */
public class StanfordNLPLemmatizer {

    private static StanfordNLPLemmatizer stanfordNLPLemmatizer = null;
    private StanfordCoreNLP pipeline;

    private StanfordNLPLemmatizer() {
        Properties props;
        props = new Properties();
        props.put("annotators", "tokenize,ssplit,pos,lemma");

        this.pipeline = new StanfordCoreNLP(props);
    }

    private static void init() {
        if (stanfordNLPLemmatizer == null) {
            stanfordNLPLemmatizer = new StanfordNLPLemmatizer();
        }
    }

    public static List<String> lemmatize(String docText) {
        init();

        List<String> lemmas = new LinkedList<>();

        Annotation document = new Annotation(docText);
        stanfordNLPLemmatizer.pipeline.annotate(document);

        List<CoreMap> sentences = document.get(CoreAnnotations.SentencesAnnotation.class);
        for (CoreMap sentence : sentences) {
            for (CoreLabel token : sentence.get(CoreAnnotations.TokensAnnotation.class)) {
                lemmas.add(token.get(CoreAnnotations.LemmaAnnotation.class));
            }
        }
        return lemmas;
    }

}
