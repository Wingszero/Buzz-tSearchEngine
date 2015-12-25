package com.buzzit.ranker;

import com.buzzit.indexer.wiki.KnowledgeEntity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class QueryResultItems implements Serializable {
    private String query;
    private List<QueryPageEntity> webPageEntities;
    private KnowledgeEntity knowledgeEntity;

    public QueryResultItems(String query) {
        this.query = query;
        webPageEntities = new ArrayList<>();
        knowledgeEntity = null;
    }

    public void addKnowledgeEntity(KnowledgeEntity entity) {
        knowledgeEntity = entity;
    }

    public void addPageEntity(QueryPageEntity entity) {
        webPageEntities.add(entity);
    }

    public String getQuery() {
        return query;
    }

    public List<QueryPageEntity> getWebPageEntities() {
        return webPageEntities;
    }

    public KnowledgeEntity getKnowledgeEntity() {
        return knowledgeEntity;
    }
}
