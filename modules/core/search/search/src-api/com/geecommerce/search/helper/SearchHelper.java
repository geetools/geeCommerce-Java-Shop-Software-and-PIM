package com.geecommerce.search.helper;

import java.util.List;
import java.util.Map;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.facet.FacetBuilder;

import com.geecommerce.core.system.attribute.model.Attribute;
import com.geecommerce.search.model.Facet;

public interface SearchHelper {

    public List<Facet> buildFacets(SearchResponse response);

    public Map<String, Object> toFilterMap(String filterParam);

    public List<FacetBuilder> toFacetBuilders(Map<String, Attribute> facetAttributes);

    public QueryBuilder toAndQuery(Map<String, Object> queryParams);

    public QueryBuilder toAndQuery(Map<String, Object> queryParams, boolean ignoreVisibilityAndStatus);
}
