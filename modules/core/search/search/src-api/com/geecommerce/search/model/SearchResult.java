package com.geecommerce.search.model;

import java.util.List;

import com.geecommerce.core.service.api.Injectable;

public interface SearchResult extends Injectable {
    public SearchResult addDocumentId(Object documentId);

    public List<Object> getDocumentIds();

    public List<Facet> getFacets();

    public SearchResult setFacets(List<Facet> facets);

    public SearchResult setTotalNumResults(long totalHits);

    public long getTotalNumResults();
}
