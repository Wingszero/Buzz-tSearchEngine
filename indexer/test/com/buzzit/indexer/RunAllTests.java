package com.buzzit.Indexer;

import com.buzzit.Indexer.db.DBExtractorTest;
import com.buzzit.Indexer.db.DBWrapperTest;
import com.buzzit.Indexer.db.WebpageContentFormatterTest;
import org.junit.runner.JUnitCore;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        TfIdfTest.class,
        IndexerTest.class,
        KnowledgeGraphTest.class,
        PorterStemmerTest.class,
        StanfordNLPLemmatizerTest.class,
        DBExtractorTest.class,
        DBWrapperTest.class,
        WebpageContentFormatterTest.class
})

public class RunAllTests {
    public static void main(String args[]) {
        JUnitCore.runClasses(RunAllTests.class);
    }
}
