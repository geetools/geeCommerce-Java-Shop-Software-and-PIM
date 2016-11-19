package com.geecommerce.search.model;

import java.util.ArrayList;
import java.util.List;

import com.geecommerce.core.service.annotation.Injectable;

@Injectable
public class DefaultSearchResult implements SearchResult {
    private static final long serialVersionUID = 2623439771010068055L;
    private List<Object> documentIds = new ArrayList<>();
    private List<Facet> facets = null;
    private long totalNumResults = 0;

    @Override
    public SearchResult addDocumentId(Object documentId) {
	documentIds.add(documentId);
	return this;
    }

    @Override
    public List<Object> getDocumentIds() {
	return documentIds;
    }

    @Override
    public List<Facet> getFacets() {
	return facets;
    }

    @Override
    public SearchResult setFacets(List<Facet> facets) {
	this.facets = facets;
	return this;
    }

    @Override
    public SearchResult setTotalNumResults(long totalNumResults) {
	this.totalNumResults = totalNumResults;
	return this;
    }

    @Override
    public long getTotalNumResults() {
	return totalNumResults;
    }
}