package com.buzzit.indexer.db;

/**
 *
 */
public class WebpageContentFormatter {
    public static String getFormatContent(String content) {
        content = content.replaceAll("\t|\r|\n|.|,|(|)|:|;|&", " ");
        return content;
    }
}
