package com.geecommerce.core.elasticsearch.search;

import com.geecommerce.core.elasticsearch.api.search.Facet;
import com.geecommerce.core.elasticsearch.api.search.FacetEntry;
import com.geecommerce.core.elasticsearch.api.search.SearchResult;
import com.geecommerce.core.service.annotation.Injectable;
import com.google.common.collect.Sets;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

@Injectable
public class DefaultSearchResult implements SearchResult {
    private static final long serialVersionUID = -3474188336678587084L;
    private Set<Object> documentIds = Sets.newLinkedHashSet();
    private List<Facet> facets = null;
    private long totalNumResults = 0;

    @Override
    public SearchResult addDocumentId(Object documentId) {
        documentIds.add(documentId);
        return this;
    }

    @Override
    public Set<Object> getDocumentIds() {
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
    public List<Facet> getActiveFacets() {
        List<Facet> activeFacets = new LinkedList<>();
        if (facets != null) {
            for (Facet facet : facets) {
                List<FacetEntry> entries = facet.getEntries();
                if (entries != null && entries.size() > 1) {
                    activeFacets.add(facet);
                }
            }
        }
        return activeFacets;
    }

    @Override
    public long getTotalNumResults() {
        return totalNumResults;
    }

    @Override
    public SearchResult setTotalNumResults(long totalNumResults) {
        this.totalNumResults = totalNumResults;
        return this;
    }
}
