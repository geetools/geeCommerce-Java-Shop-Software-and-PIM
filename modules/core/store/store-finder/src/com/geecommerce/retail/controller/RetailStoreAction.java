package com.geecommerce.retail.controller;

import java.util.List;
import java.util.stream.Collectors;

import com.geecommerce.retail.model.RetailStoreCertificateInformation;
import com.geecommerce.retail.service.LocationService;
import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.HandlesEvent;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;
import com.google.inject.Inject;
import com.geecommerce.core.web.BaseActionBean;
import com.geecommerce.mediaassets.service.MediaAssetService;
import com.geecommerce.retail.model.RetailStore;
import com.geecommerce.retail.service.RetailStoreService;

@UrlBinding("/store/{$event}/{id}")
public class RetailStoreAction extends BaseActionBean {
    private RetailStoreView retailStoreView;
    private List<RetailStoreView> retailStoreViews;

    private String zip;
    private RetailStoreCertificateInformation certificateInformation;
    private RetailStore retailStore;
    private List<RetailStore> stores;

    private final RetailStoreService retailStoreService;
    private final MediaAssetService mediaAssetService;
    private final LocationService locationService;

    @Inject
    public RetailStoreAction(RetailStoreService retailStoreService, MediaAssetService mediaAssetService, LocationService locationService) {
	this.retailStoreService = retailStoreService;
	this.mediaAssetService = mediaAssetService;
	this.locationService = locationService;
    }

    @HandlesEvent("form")
    public Resolution form() {
	stores = retailStoreService.getRetailStores();
	return view("retail_stores/find_form");
    }

    @HandlesEvent("find")
    public Resolution find() {
	if (getZip() != null) {
	    stores = retailStoreService.findClosestByZipCode(getZip(), 6);
	} else {
	    stores = retailStoreService.getRetailStores();
	    stores.stream().forEach(retailStore1 -> retailStore1.setDistance(null));
	}
	return view("retail_stores/result");
    }

    @DefaultHandler
    public Resolution view() {
	if (getId() != null) {
	    RetailStore retailStore = retailStoreService.getRetailStore(getId());
	    this.retailStore = retailStore;
	    stores = retailStoreService.getStoresHavingField(RetailStore.Column.ZIP);
	    RetailStoreViewBuilder retailStoreViewBuilder = new RetailStoreViewBuilder(mediaAssetService, locationService);
	    retailStoreView = retailStoreViewBuilder.createRetailStoreView(retailStore).build();
	    certificateInformation = retailStoreService.getCertificateInformation();
	}
	return view("retail_stores/view");// , "6h");
    }

    public RetailStoreView getRetailStoreView() {
	return retailStoreView;
    }

    public void setRetailStoreView(RetailStoreView retailStoreView) {
	this.retailStoreView = retailStoreView;
    }

    public List<RetailStoreView> getRetailStoreViews() {
	return retailStoreViews;
    }

    public void setRetailStoreViews(List<RetailStoreView> retailStoreViews) {
	this.retailStoreViews = retailStoreViews;
    }

    public String getZip() {
	return zip;
    }

    public void setZip(String zip) {
	this.zip = zip;
    }

    public RetailStore getRetailStore() {
	return retailStore;
    }

    public void setRetailStore(RetailStore retailStore) {
	this.retailStore = retailStore;
    }

    public List<RetailStore> getStores() {
	return stores;
    }

    public void setStores(List<RetailStore> stores) {
	this.stores = stores;
    }

    public RetailStoreCertificateInformation getCertificateInformation() {
	return certificateInformation;
    }

    public void setCertificateInformation(RetailStoreCertificateInformation certificateInformation) {
	this.certificateInformation = certificateInformation;
    }
}
