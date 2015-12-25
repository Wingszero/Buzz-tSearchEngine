package com.buzzit.ranker;

import com.buzzit.indexer.db.DBLookup;
import com.myapp.worker.db.WebPageEntity;
import scala.Tuple2;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class QueryPageEntityFactory {
    // Create query page entity from url and query.
    public static List<QueryPageEntity> constructPageEntities(List<String> urls, String query) {
        List<WebPageEntity> pages = DBLookup.getInstance().getPages(urls);
        List<QueryPageEntity> pageEntities = new ArrayList<>();
        pages.forEach(page -> pageEntities.add(
                new QueryPageEntity(page.getTitle(), page.getUrl(),
                        page.getContent(), query)));
        return pageEntities;
    }
}
