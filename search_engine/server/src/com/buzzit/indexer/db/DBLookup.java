package com.buzzit.indexer.db;

import com.myapp.worker.db.WebPageEntity;
import scala.Tuple2;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 *
 */
public class DBLookup {
    private static DBLookup lookup = null;

    private List<String> dbPathList;
    private ExecutorService executor;
    private List<DBLookupTask> tasks;

    private DBLookup() {
        dbPathList = DBConfig.getInstance().DB_PATH_LIST;

        executor = Executors.newFixedThreadPool(dbPathList.size());
        tasks = new ArrayList<>();
        for (String dbPath : dbPathList) {
            DBLookupTask task = new DBLookupTask(dbPath);
            tasks.add(task);
        }
    }

    public static DBLookup getInstance() {
        if (lookup == null) {
            lookup = new DBLookup();
        }
        return lookup;
    }

    public synchronized List<WebPageEntity> getPages(List<String> urls) {
        List<Future> futures = new ArrayList<>();
        // Start tasks
        for (DBLookupTask task : tasks) {
            task.setLookupUrls(urls);
            futures.add(executor.submit(task));
        }
        // wait for complete
        for (Future future : futures) {
            try {
                future.get();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        List<WebPageEntity> pages = new ArrayList<>(urls.size());
        for (int i=0; i<urls.size(); ++i) {
            pages.add(null);
        }

        HashSet<String> addedUrl = new HashSet<>();
        for (DBLookupTask task : tasks) {
            task.getResult().forEach(page -> {
                if (!addedUrl.contains(page.getUrl())) {
                    pages.set(urls.indexOf(page.getUrl()), page);
                    addedUrl.add(page.getUrl());
                }
            });
        }
        return pages;
    }
}
