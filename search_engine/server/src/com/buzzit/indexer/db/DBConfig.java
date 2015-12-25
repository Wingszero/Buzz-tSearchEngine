package com.buzzit.indexer.db;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class DBConfig {
    private static DBConfig config = null;

    private static final String DB_CONF_PATH = "db.conf";

    public static final String CORPUS_PATH = "corpus";

    public List<String> DB_PATH_LIST;

    private DBConfig() {
        DB_PATH_LIST = new ArrayList<>();
        readDBPathFromFile();
    }

    public static DBConfig getInstance() {
        if (config == null) {
            config = new DBConfig();
        }
        return config;
    }

    private void readDBPathFromFile() {
        try {
            File confFile = new File(DB_CONF_PATH);
            System.out.println("Reading DB path from file...");
            BufferedReader in = new BufferedReader(new FileReader(confFile));
            String line;
            while ((line = in.readLine()) != null) {
                System.out.println("path: " + line);
                DB_PATH_LIST.add(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
