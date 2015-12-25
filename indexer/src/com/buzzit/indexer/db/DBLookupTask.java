package com.buzzit.indexer.db;

import com.myapp.worker.db.WebPageEntity;
import com.sleepycat.persist.PrimaryIndex;
import scala.Tuple2;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class DBLookupTask implements Runnable {
    private DBWrapper wrapper;
    private PrimaryIndex<String, WebPageEntity> allPages;
    private List<String> urls;
    private List<WebPageEntity> pageResult;

    public DBLookupTask(String path) {
        wrapper = new DBWrapper(path);
        allPages = wrapper.getWebPageIdx();
    }

    public void setLookupUrls(List<String> urls) {
        this.urls = urls;
    }

    public List<WebPageEntity> getResult() {
        return pageResult;
    }

    @Override
    public void run() {
        pageResult = new ArrayList<>();
        for (String url : urls) {
            WebPageEntity pageEntity = allPages.get(url);
            if (pageEntity != null) {
                pageResult.add(pageEntity);
            }
        }
    }
}
