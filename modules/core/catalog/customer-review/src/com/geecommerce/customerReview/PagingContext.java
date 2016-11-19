package com.geecommerce.customerReview;

public interface PagingContext {

    public int getPage();

    public void setPage(int page);

    public String getPagingURI();

    public int getOffset();

    public int getLimit();

    public void setLimit(int limit);

    public int getDefaultNumResultsPerPage();

    public long getTotalNumResults();

    public void setTotalNumResults(long numResults);

    public int getNumResultsPerPage();

    public int[] getNumResultsPerPageList();

    public int getResultsFrom();

    public int getResultsTo();

    public int getNumPages();

}
