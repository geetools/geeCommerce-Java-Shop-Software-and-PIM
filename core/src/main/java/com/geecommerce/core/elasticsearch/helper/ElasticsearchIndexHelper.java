package com.geecommerce.core.elasticsearch.helper;

import java.util.Map;
import java.util.Set;

import com.geecommerce.core.elasticsearch.api.SearchIndexSupport;
import com.geecommerce.core.service.api.Model;
import com.geecommerce.core.system.attribute.model.Attribute;
import com.geecommerce.core.system.attribute.model.AttributeValue;
import com.geecommerce.core.type.Id;

public interface ElasticsearchIndexHelper {

    public <T extends Model> String indexName(Id merchantId, Id storeId, Class<T> modelClass);

    public String indexName(Long merchantId, Long storeId, String name);

    public Map<String, Set<String>> getIndexAliases();

    public void updateIndexedItem(SearchIndexSupport indexedItem);

    public void updateIndexedItem(SearchIndexSupport indexedItem, boolean allIndexes);

    public void removeIndexedItem(SearchIndexSupport indexedItem);

    public void updateIndex(Map<String, Object> indexerContext, SearchIndexSupport indexedItem);

    public void updateIndex(String indexName, SearchIndexSupport indexedItem);

    public void removeFromIndex(Map<String, Object> indexerContext, SearchIndexSupport indexedItem);

    public void removeFromIndex(String indexName, SearchIndexSupport indexedItem);

    public void createNewIndex(String indexName);

    public boolean indexExists(String indexName);

    public String[] getAllIndexes();

    public void cleanupOldIndexes(String newIndexName, Map<String, Set<String>> indexAliases);

    public void createIndexAlias(String indexName, String aliasName);

    public void removeIndexAlias(String indexName, String aliasName);

    public void activateIndex(String indexName, String aliasName);

    public void waitForGreenStatus(String indexName);

    public void waitForStatus(String indexName, String status);

    public void flushIndex(String indexName);

    public void updateLastIndexedDate(Map<String, Object> updateData);

    public void disableDateDetection(String indexName, String name);

    public void addAttribute(Map<String, Object> json, Attribute attribute, AttributeValue attributeValue);

    public void addAttributeOptions(Map<String, Object> json, Attribute attribute, AttributeValue attributeValue);

}
