package com.geecommerce.catalog.product;

public interface FilterContext {

    String getSort();

    FilterContext setSort(String sort);

    Double getPriceFrom();

    FilterContext setPriceFrom(Double priceFrom);

    Double getPriceTo();

    FilterContext setPriceTo(Double priceTo);
}
