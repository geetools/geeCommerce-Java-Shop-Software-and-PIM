package com.geecommerce.catalog.product;

import java.util.List;

public interface PagingContext {
    public int getPage();

    public String getPagingURI();

    public int getOffset();

    public int getLimit();

    public int getNumResultsPerPage();

    public List<Integer> getNumResultsPerPageList();
}
