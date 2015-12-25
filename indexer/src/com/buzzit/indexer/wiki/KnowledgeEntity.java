package com.buzzit.indexer.wiki;

import java.io.Serializable;

/**
 *
 */
public class KnowledgeEntity implements Serializable {
    private String title;
    private String content;
    private String url;

    public KnowledgeEntity(String title, String content, String url) {
        this.title = title;
        this.content = content;
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public String getUrl() {
        return url;
    }
}
