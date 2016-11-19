package com.geecommerce.core.elasticsearch.service;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.elasticsearch.index.query.FilterBuilder;

import com.geecommerce.core.elasticsearch.api.search.SearchResult;
import com.geecommerce.core.elasticsearch.search.SearchParams;
import com.geecommerce.core.service.api.Model;
import com.geecommerce.core.system.attribute.model.Attribute;
import com.geecommerce.core.type.Id;

public interface ElasticsearchService {

    public <T extends Model> SearchResult findItems(Class<T> modelClass, List<FilterBuilder> filterBuilders, Map<String, Attribute> filterAttributes, Map<String, Object> navFilterParts, Map<String, Set<Object>> uriFilterParts, SearchParams searchParams, Integer offset, Integer limit, String sort);

    public <T extends Model> SearchResult findItems(Class<T> modelClass, List<FilterBuilder> filterBuilders, Map<String, Attribute> filterAttributes, Map<String, Object> navFilterParts, Map<String, Set<Object>> uriFilterParts, SearchParams searchParams);

    public <T extends Model> SearchResult findItems(Class<T> modelClass, String query, SearchParams searchParams, List<Id> attributeTargetObjectIds);
}
