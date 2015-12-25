package com.buzzit;

import com.buzzit.indexer.*;
import com.buzzit.indexer.cache.*;
import com.buzzit.indexer.wiki.KnowledgeGraphTest;
import com.buzzit.indexer.db.DBExtractorTest;
import com.buzzit.indexer.db.DBWrapperTest;
import com.buzzit.indexer.db.WebpageContentFormatterTest;
import com.buzzit.indexer.wiki.KnowledgeTfIdfTest;
import com.buzzit.ranker.QueryPageEntityTest;
import com.buzzit.ranker.RankerTest;
import org.apache.commons.io.FileUtils;
import org.junit.runner.JUnitCore;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import java.io.File;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        TfIdfTest.class,
        KnowledgeTfIdfTest.class,
        IndexerTest.class,
        KnowledgeGraphTest.class,
        RankerTest.class,
        PorterStemmerTest.class,
        DBExtractorTest.class,
        DBWrapperTest.class,
        WebpageContentFormatterTest.class,
        HeapSortTest.class,
        CentralCacheTest.class,
        DistributedCacheTest.class,
        QueryResultCacheTest.class,
        QueryPageEntityTest.class,
        SearchEngineTest.class
})

public class RunAllTests {
    public static void removeSavedFiles() {
        try {
            if (ConfigLoader.getRecompute()) {
                File tfidfPostings = new File(ConfigLoader.getHDFS() +
                        ConfigLoader.getTfidfPath() + "/postings");
                if (tfidfPostings.exists()) FileUtils.deleteDirectory(tfidfPostings);
                File tfidfChampionList = new File(ConfigLoader.getHDFS() +
                        ConfigLoader.getTfidfPath() + "/championList");
                if (tfidfChampionList.exists()) FileUtils.deleteDirectory(tfidfChampionList);

                File wikiPostings = new File(ConfigLoader.getHDFS() +
                        ConfigLoader.getWikiTfidfPath() + "/postings");
                if (wikiPostings.exists()) FileUtils.deleteDirectory(wikiPostings);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String args[]) {
        JUnitCore.runClasses(RunAllTests.class);
    }
}


