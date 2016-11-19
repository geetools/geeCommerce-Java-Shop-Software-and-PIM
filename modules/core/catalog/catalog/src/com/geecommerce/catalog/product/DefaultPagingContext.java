package com.geecommerce.catalog.product;

import java.util.Arrays;
import java.util.List;

import com.geecommerce.core.App;
import com.geecommerce.core.service.annotation.Pojo;
import com.google.inject.Inject;

@Pojo
public class DefaultPagingContext implements PagingContext {
    @Inject
    protected App app;

    protected int page = 1;

    protected int limit = 0;

    protected String pagingURI;

    @SuppressWarnings({ "unchecked", "rawtypes" })
    protected static final List<Integer> defaultLimitPerPage = (List) Arrays.asList(new Integer[] { 24, 48, 72 });

    private static final String LIMIT_PER_PAGE_KEY = "catalog/product_list/pagination/limit_per_page";
    private static final String LIMIT_PER_PAGE_LIST_KEY = "catalog/product_list/pagination/limit_per_page_list";

    @Override
    public int getPage() {
        return page;
    }

    @Override
    public String getPagingURI() {
        return pagingURI;
    }

    @Override
    public int getOffset() {
        return (getPage() - 1) * getNumResultsPerPage();
    }

    @Override
    public int getLimit() {
        return limit;
    }

    @Override
    public int getNumResultsPerPage() {
        String cookie = app.cookieGet("limit_per_page");
        return cookie != null ? Integer.valueOf(cookie) : getLimit() > 0 ? getLimit() : defaultNumResultsPerPage();
    }

    @Override
    public List<Integer> getNumResultsPerPageList() {
        return app.cpIntList_(LIMIT_PER_PAGE_LIST_KEY, defaultLimitPerPage);
    }

    protected int defaultNumResultsPerPage() {
        return app.cpInt_(LIMIT_PER_PAGE_KEY, 24);
    }
}
