package com.geecommerce.core.elasticsearch.search;

import com.geecommerce.core.elasticsearch.api.search.FacetEntry;
import com.geecommerce.core.service.annotation.Injectable;

@Injectable
public class DefaultFacetEntry implements FacetEntry, Comparable<FacetEntry> {
    private static final long serialVersionUID = 2365506266644409015L;
    private String id = null;
    private String label = null;
    private Double rangeFrom = null;
    private Double rangeTo = null;
    private long count = 0;
    private long nonMultiCount = 0;

    @Override
    public FacetEntry values(String id, String label, long count, long nonMultiCount) {
        this.id = id;
        this.label = label;
        this.count = count;
        this.nonMultiCount = nonMultiCount;

        return this;
    }

    @Override
    public FacetEntry values(String id, String label, Double rangeFrom, Double rangeTo, long count,
        long nonMultiCount) {
        this.id = id;
        this.label = label;
        this.rangeFrom = rangeFrom;
        this.rangeTo = rangeTo;
        this.count = count;
        this.nonMultiCount = nonMultiCount;

        return this;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getLabel() {
        return label;
    }

    @Override
    public DefaultFacetEntry setLabel(String label) {
        this.label = label;
        return this;
    }

    @Override
    public Double getRangeFrom() {
        return rangeFrom;
    }

    @Override
    public Double getRangeTo() {
        return rangeTo;
    }

    @Override
    public long getCount() {
        return count;
    }

    @Override
    public long getNonMultiCount() {
        return nonMultiCount;
    }

    @Override
    public int compareTo(FacetEntry entry) {
        if (entry.getLabel() == null && this.getLabel() == null) {
            return 0;
        }

        if (this.getLabel() == null) {
            return 1;
        }

        if (entry.getLabel() == null) {
            return -1;
        }

        return this.getLabel().compareTo(entry.getLabel());
    }
}
