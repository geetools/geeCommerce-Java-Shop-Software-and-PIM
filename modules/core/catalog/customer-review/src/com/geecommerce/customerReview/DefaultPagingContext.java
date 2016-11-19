package com.geecommerce.customerReview;

import com.geecommerce.core.App;
import com.geecommerce.core.service.annotation.Pojo;
import com.google.inject.Inject;

@Pojo
public class DefaultPagingContext implements PagingContext {
    @Inject
    protected App app;

    public int page = 1;

    public int limit = 0;

    public String pagingURI;

    public long totalNumResults = 0;

    public String getPagingURI() {
        return pagingURI;
    }

    public int getDefaultNumResultsPerPage() {
        return 5;
    }

    public long getTotalNumResults() {
        return this.totalNumResults;
    }

    public void setTotalNumResults(long totalNumResults) {
        this.totalNumResults = totalNumResults;
    }

    public int[] getNumResultsPerPageList() {
        return new int[] { 5, 10, 20 };
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getPage() {
        return page > getNumPages() && getNumPages() != 0 ? getNumPages() : page;
    }

    public int getNumPages() {
        return (int) (getTotalNumResults() + getRealNumResultsPerPage() - 1) / getRealNumResultsPerPage();
    }

    public int getNumResultsPerPage() {
        String cookie = app.cookieGet("limit_per_page");
        return cookie != null ? Integer.valueOf(cookie) : getLimit() > 0 ? getLimit() : getDefaultNumResultsPerPage();
    }

    public int getRealNumResultsPerPage() {
        return getNumResultsPerPage();
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public int getLimit() {
        return limit;
    }

    public int getResultsFrom() {
        return getOffset() + 1;
    }

    public int getResultsTo() {
        int to = getOffset() + getRealNumResultsPerPage();
        return to > getTotalNumResults() ? (int) getTotalNumResults() : to;
    }

    public int getOffset() {
        return (getPage() - 1) * getRealNumResultsPerPage();
    }
}
