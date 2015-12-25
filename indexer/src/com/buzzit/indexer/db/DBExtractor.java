package com.buzzit.indexer.db;

import com.myapp.worker.db.WebPageEntity;
import com.sleepycat.persist.EntityCursor;
import com.sleepycat.persist.PrimaryIndex;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * This class is used to read page data from all crawler workers, and merge them into a large file.
 * We attach all crawler worker EBS onto our indexer master node.
 * With path to each DB provided as input in the file db.conf.
 */
public class DBExtractor {
    private static final String CORPUS_PATH = DBConfig.CORPUS_PATH;

    private List<String> db_path_list;

    public DBExtractor() {
        db_path_list = DBConfig.getInstance().DB_PATH_LIST;
    }

    public void extractDBToFile(String filePath) {
        try {
            File outFile = new File(filePath);
            BufferedWriter out = new BufferedWriter(new FileWriter(outFile));

            for (String inFilePath : db_path_list) {
                System.out.println("Extracting db: " + inFilePath);
                DBWrapper wrapper = new DBWrapper(inFilePath);
                PrimaryIndex<String, WebPageEntity> allPages = wrapper.getWebPageIdx();
                EntityCursor<WebPageEntity> cursor = allPages.entities();
                Iterator<WebPageEntity> iter = cursor.iterator();
                int counter = 0;
                while (iter.hasNext()) {
                    WebPageEntity page = iter.next();
                    String url = page.getName();
                    String title = WebpageContentFormatter.getFormatContent(page.getTitle());
                    String formatContent = WebpageContentFormatter.getFormatContent(page.getContent());
                    // Format: url::title\tcontent
                    out.write(url + "::" + title + "\t" + formatContent + "\n");
                    if (counter % 10000 == 0) {
                        System.out.println("Extracted " + counter + " pages..");
                    }
                    counter++;
                }
                cursor.close();
                wrapper.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String argv[]) {
        DBExtractor dbExtractor = new DBExtractor();
        dbExtractor.extractDBToFile(CORPUS_PATH);
    }
}
