package com.geecommerce.core.elasticsearch.api.search;

import java.util.List;

import com.geecommerce.core.service.api.Injectable;

public interface Facet extends Injectable, Comparable<Facet> {
    public Facet values(String code, String label, int position);

    public String getCode();

    public String getLabel();

    public boolean isRange();

    public int getPosition();

    public long getTotalCount();

    public long getEntryCount();

    public Facet addEntry(String id, String label, long count, long multiCount);

    public Facet addRangeEntry(String id, String label, Double from, Double to, long count, long multiCount);

    public List<FacetEntry> getEntries();

    public void sortEntries();
}
