package com.geecommerce.core.elasticsearch.helper;


import com.geecommerce.core.elasticsearch.api.search.Facet;
import com.geecommerce.core.elasticsearch.search.FacetCount;
import com.geecommerce.core.elasticsearch.search.FieldKey;
import com.geecommerce.core.elasticsearch.search.FieldValue;
import com.geecommerce.core.elasticsearch.search.FilterValue;
import com.geecommerce.core.system.attribute.model.Attribute;
import com.geecommerce.core.type.Id;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.FilterBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.facet.FacetBuilder;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface ElasticsearchHelper {

    public Id[] toIds(Object[] documentIds);

    public Map<String, FilterValue> toQueryMap(Map<String, Object> navFilterParts, Map<String, Attribute> filterAttributes);

    public Map<String, FilterValue> toQueryMap(Map<String, Object> navFilterParts, Map<String, Set<Object>> uriFilterParts, Map<String, Attribute> filterAttributes);

    public QueryBuilder buildQuery(List<FilterBuilder> builders, Map<String, FilterValue> filterParams);

    public List<Facet> retrieveFacets(SearchResponse responseAll, Map<FieldKey, FieldValue> fieldIndex, Map<FieldKey, FacetCount> facetCountIndex, Map<String, Attribute> filterAttributes);

    public String[] toFieldList(Map<String, Attribute> filterAttributes);

    public List<FacetBuilder> toFacetBuilders(Map<String, Attribute> filterAttributes);

    public Map<FieldKey, FieldValue> toFlatFieldIndex(SearchResponse response, SearchResponse responseAll, Map<String, Attribute> filterAttributes);

    public Map<FieldKey, FacetCount> toFlatFacetCountIndex(SearchResponse response, SearchResponse responseNonMulti, SearchResponse responseAll);

    public Map<String, String> buildAttributeAliasIndex(Map<String, Attribute> filterAttributes);

    public <T> T sanitize(T o);

    public QueryBuilder toAndQuery(Map<String, Object> queryParams);
}
