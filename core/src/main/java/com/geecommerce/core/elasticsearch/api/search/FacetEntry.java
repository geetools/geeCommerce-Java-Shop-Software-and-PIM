package com.geecommerce.core.elasticsearch.api.search;

import com.geecommerce.core.service.api.Injectable;

public interface FacetEntry extends Injectable, Comparable<FacetEntry> {
    public FacetEntry values(String id, String label, long count, long multiCount);

    public FacetEntry values(String id, String label, Double rangeFrom, Double rangeTo, long count, long multiCount);

    public String getId();

    public String getLabel();

    public FacetEntry setLabel(String label);

    public Double getRangeFrom();

    public Double getRangeTo();

    public long getCount();

    public long getNonMultiCount();
}
