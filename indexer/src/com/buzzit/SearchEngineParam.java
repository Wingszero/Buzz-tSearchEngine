package com.buzzit;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

/**
 *
 */
public class SearchEngineParam {
    protected static void init() {
        try {
            File confFile = new File("buzzit.conf");
            System.out.println("Reading parameters from file...");
            BufferedReader in = new BufferedReader(new FileReader(confFile));

            String line;

            while ((line = in.readLine()) != null) {
                String pair[] = line.split("\\s+");
                switch(pair[0]) {
                    case "title.weight":
                        TITLE_WEIGHT = Double.parseDouble(pair[1]);
                        break;
                    case "url.weight":
                        URL_WEIGHT = Double.parseDouble(pair[1]);
                        break;
                    case "query.result.length":
                        QUERY_RESULT_LENGTH = Integer.parseInt(pair[1]);
                        break;
                    case "posting.expand.factor":
                        POSTING_EXPAND_FACTOR = Double.parseDouble(pair[1]);
                        break;
                    case "posting.length":
                        POSTING_LENGTH = Integer.parseInt(pair[1]);
                        break;
                    case "hierarchy.depth":
                        HIERARCHY_DEPTH = Integer.parseInt(pair[1]);
                        break;
                    case "champion.list.threshold":
                        CHAMPION_LIST_THRESHOLD = Integer.parseInt(pair[1]);
                        break;
                    default:
                        break;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * TfIdf parameters.
     */
    private static double TITLE_WEIGHT = 2;
    private static double URL_WEIGHT = 2;
    private static int QUERY_RESULT_LENGTH = 100;
    private static int POSTING_LENGTH = 100;
    private static double POSTING_EXPAND_FACTOR = 1;
    private static int HIERARCHY_DEPTH = 3;
    private static int CHAMPION_LIST_THRESHOLD = 2;

    public static double getPostingExpandFactor() {
        return POSTING_EXPAND_FACTOR;
    }

    public static double getTitleWeight() {
        return TITLE_WEIGHT;
    }

    public static int getChampionListThreshold() {
        return CHAMPION_LIST_THRESHOLD;
    }

    public static double getUrlWeight() {

        return URL_WEIGHT;
    }

    public static int getQueryResultLength() {
        return QUERY_RESULT_LENGTH;
    }

    public static int getPostingLength() {
        return POSTING_LENGTH;
    }

    public static int getHierarchyDepth() {
        return HIERARCHY_DEPTH;
    }
}
