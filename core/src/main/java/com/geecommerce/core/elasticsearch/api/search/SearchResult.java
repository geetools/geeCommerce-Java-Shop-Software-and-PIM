package com.geecommerce.core.elasticsearch.api.search;

import java.util.List;
import java.util.Set;

import com.geecommerce.core.service.api.Injectable;

public interface SearchResult extends Injectable {
    public SearchResult addDocumentId(Object documentId);

    public Set<Object> getDocumentIds();

    public List<Facet> getFacets();

    public SearchResult setFacets(List<Facet> facets);

    public List<Facet> getActiveFacets();

    public long getTotalNumResults();

    public SearchResult setTotalNumResults(long totalNumResults);
}
