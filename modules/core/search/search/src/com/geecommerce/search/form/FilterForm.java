package com.geecommerce.search.form;

public class FilterForm {
    private String sort;

    private Double priceFrom;

    private Double priceTo;

    private boolean showAll = true;

    private boolean showEvent = false;

    private boolean showSale = false;

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public Double getPriceFrom() {
        return priceFrom;
    }

    public void setPriceFrom(Double priceFrom) {
        this.priceFrom = priceFrom;
    }

    public Double getPriceTo() {
        return priceTo;
    }

    public void setPriceTo(Double priceTo) {
        this.priceTo = priceTo;
    }

    public boolean isShowAll() {
        return showAll;
    }

    public void setShowAll(boolean showAll) {
        this.showAll = showAll;
    }

    public boolean isShowEvent() {
        return showEvent;
    }

    public void setShowEvent(boolean showEvent) {
        this.showEvent = showEvent;
    }

    public boolean isShowSale() {
        return showSale;
    }

    public void setShowSale(boolean showSale) {
        this.showSale = showSale;
    }
}
