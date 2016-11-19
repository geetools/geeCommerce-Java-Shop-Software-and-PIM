package com.geecommerce.core.elasticsearch.search;

import java.io.Serializable;

public class FacetCount implements Serializable {
    private static final long serialVersionUID = 370934366559880545L;

    private final int count;
    private final int nonMultiCount;
    private final int allCount;

    public FacetCount(int count, int nonMultiCount, int allCount) {
        this.count = count;
        this.nonMultiCount = nonMultiCount;
        this.allCount = allCount;
    }

    public int getCount() {
        return count;
    }

    public int getNonMultiCount() {
        return nonMultiCount;
    }

    public int getAllCount() {
        return allCount;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + allCount;
        result = prime * result + count;
        result = prime * result + nonMultiCount;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;

        if (obj == null)
            return false;

        if (getClass() != obj.getClass())
            return false;

        FacetCount other = (FacetCount) obj;

        if (allCount != other.allCount)
            return false;

        if (count != other.count)
            return false;

        if (nonMultiCount != other.nonMultiCount)
            return false;

        return true;
    }
}
