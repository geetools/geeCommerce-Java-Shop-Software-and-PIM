package com.geecommerce.catalog.product;

import com.geecommerce.core.service.annotation.Pojo;

@Pojo
public class DefaultFilterContext implements FilterContext {
    private String sort;

    private Double priceFrom;

    private Double priceTo;

    public String getSort() {
        return sort;
    }

    public FilterContext setSort(String sort) {
        this.sort = sort;
        return this;
    }

    public Double getPriceFrom() {
        return priceFrom;
    }

    public FilterContext setPriceFrom(Double priceFrom) {
        this.priceFrom = priceFrom;
        return this;
    }

    public Double getPriceTo() {
        return priceTo;
    }

    public FilterContext setPriceTo(Double priceTo) {
        this.priceTo = priceTo;
        return this;
    }
}
