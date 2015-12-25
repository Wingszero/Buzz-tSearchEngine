package com.buzzit.indexer;

import com.buzzit.indexer.stemmer.PorterStemmer;
import org.apache.commons.io.IOUtils;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import static org.junit.Assert.*;

/**
 *
 */
public class PorterStemmerTest {
    @Test
    public void testLemmatizer() {
        File file = new File("../testdir/small");
        try {
            FileInputStream in = new FileInputStream(file);
            String docText = IOUtils.toString(in);
            List<String> words = PorterStemmer.lemmatize(docText);
            for (String word : words) {
                System.out.println(word);
            }
        } catch (FileNotFoundException e) {
            assertEquals("success", e.getMessage());
        } catch (IOException e) {
            assertEquals("success", e.getMessage());
        }
    }

}