package com.geecommerce.retail.controller;

import com.geecommerce.core.util.Json;
import com.geecommerce.core.web.BaseActionBean;
import com.geecommerce.retail.model.RetailStore;
import com.geecommerce.retail.service.RetailStoreService;
import com.google.inject.Inject;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.StreamingResolution;
import net.sourceforge.stripes.action.UrlBinding;

/**
 *
 */
@UrlBinding("/retialstore/find_branch")
public class RetailStoreSearchAction extends BaseActionBean {

    private String zip;
    private final RetailStoreService retailStoreService;

    @Inject
    public RetailStoreSearchAction(RetailStoreService retailStoreService) {
        this.retailStoreService = retailStoreService;
    }

    @DefaultHandler
    public Resolution findBranch() {
        RetailStore store = retailStoreService.findClosestByZipCode(zip);
        return new StreamingResolution("json", store == null ? "{}" : Json.toJson(store));
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }
}
