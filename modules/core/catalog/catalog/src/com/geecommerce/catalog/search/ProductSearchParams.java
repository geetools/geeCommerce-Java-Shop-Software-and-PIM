package com.geecommerce.catalog.search;

import java.util.ArrayList;
import java.util.List;

import org.elasticsearch.index.query.FilterBuilder;
import org.elasticsearch.index.query.FilterBuilders;

import com.geecommerce.core.App;
import com.geecommerce.core.ApplicationContext;
import com.geecommerce.core.elasticsearch.search.SearchParams;
import com.geecommerce.core.system.model.RequestContext;

public class ProductSearchParams extends SearchParams {

    private Double priceFrom;
    private Double priceTo;


    public Double getPriceFrom() {
        return priceFrom;
    }

    public ProductSearchParams setPriceFrom(Double priceFrom) {
        this.priceFrom = priceFrom;
        return this;
    }

    public Double getPriceTo() {
        return priceTo;
    }

    public ProductSearchParams setPriceTo(Double priceTo) {
        this.priceTo = priceTo;
        return this;
    }

    @Override
    public String getSort() {
        if("price".equals(super.getSort())){
            return "price";
        } else {
            return getNameSearchField();
        }
    }

    protected String getNameSearchField() {
        ApplicationContext appCtx = App.get().getApplicationContext();
        RequestContext reqCtx = appCtx.getRequestContext();
        String auto = "att_name_slug" + "_" + reqCtx.getLanguage();
        return auto;
    }

    @Override
    public FilterBuilder getFilterBuilder() {
        List<FilterBuilder> filterBuilders = new ArrayList<>();

        if (this.getPriceFrom() != null || this.getPriceTo() != null) {
            filterBuilders.add(FilterBuilders.rangeFilter("price").from(this.getPriceFrom()).to(this.getPriceTo()));
        }

        return filterBuilders.size() == 0 ? null : FilterBuilders.andFilter(filterBuilders.toArray(new FilterBuilder[filterBuilders.size()]));
    }
}
