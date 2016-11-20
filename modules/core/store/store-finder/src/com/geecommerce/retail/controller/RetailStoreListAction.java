package com.geecommerce.retail.controller;

import java.util.ArrayList;
import java.util.List;

import com.geecommerce.core.web.BaseActionBean;
import com.geecommerce.mediaassets.service.MediaAssetService;
import com.geecommerce.retail.model.RetailStore;
import com.geecommerce.retail.service.RetailStoreService;
import com.google.inject.Inject;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;

@UrlBinding("/obchodni-domy/mapa-a-kontakty")
public class RetailStoreListAction extends BaseActionBean {
    private final RetailStoreService retailStoreService;

    private List<RetailStoreView> retailStoreViews = new ArrayList<>();

    private final MediaAssetService mediaAssetService;

    @Inject
    public RetailStoreListAction(RetailStoreService retailStoreService, MediaAssetService mediaAssetService) {
        this.retailStoreService = retailStoreService;
        this.mediaAssetService = mediaAssetService;
    }

    @DefaultHandler
    public Resolution view() {
        List<RetailStore> retailStores = retailStoreService.getEnabledRetailStores();
        if (retailStores != null && !retailStores.isEmpty()) {
            for (RetailStore retailStore : retailStores) {
                RetailStoreView retailStoreView = new RetailStoreView();

                // MediaAsset mediaAsset =
                // mediaAssetService.get(retailStore.getMainImageId());
                // if (mediaAsset != null)
                // retailStoreView.setRetailStoreMainImageUrl(mediaAsset.getUrl());

                retailStoreView.setId(retailStore.getId().str());
                retailStoreView.setTitle(retailStore.getTitle());
                retailStoreView.setAddressLines(retailStore.getAddressLines());
                if (retailStore.attr("opening_hours") != null)
                    retailStoreView.setOpeningHours(retailStore.attr("opening_hours").getStr());
                retailStoreView.setTelephone(retailStore.getTelephone());
                retailStoreView.setFax(retailStore.getFax());
                retailStoreView.setEmail(retailStore.getEmail());
                if (retailStore.attr("map_thumbnail_html") != null)
                    retailStoreView.setMapThumbnail(retailStore.attr("map_thumbnail_html").getStr());
                retailStoreView.setUri(retailStore.getUri());
                retailStoreViews.add(retailStoreView);
            }
        }
        return view("retail_stores/list", "6h");
    }

    public List<RetailStoreView> getRetailStoreViews() {
        return retailStoreViews;
    }

    public class RetailStoreView {
        private String id = null;
        private String retailStoreMainImageUrl = null;
        private String title = null;
        private List<String> addressLines = null;
        private String openingHours = null;
        private String telephone = null;
        private String fax = null;
        private String email = null;
        private String mapThumbnail = null;
        private String uri = null;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getUri() {
            return uri;
        }

        public void setUri(String uri) {
            this.uri = uri;
        }

        public String getMapThumbnail() {
            return mapThumbnail;
        }

        public void setMapThumbnail(String mapThumbnail) {
            this.mapThumbnail = mapThumbnail;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public List<String> getAddressLines() {
            return addressLines;
        }

        public void setAddressLines(List<String> addressLines) {
            this.addressLines = addressLines;
        }

        public String getOpeningHours() {
            return openingHours;
        }

        public void setOpeningHours(String openingHours) {
            this.openingHours = openingHours;
        }

        public String getTelephone() {
            return telephone;
        }

        public void setTelephone(String telephone) {
            this.telephone = telephone;
        }

        public String getFax() {
            return fax;
        }

        public void setFax(String fax) {
            this.fax = fax;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getRetailStoreMainImageUrl() {
            return retailStoreMainImageUrl;
        }

        public void setRetailStoreMainImageUrl(String retailStoreMainImageUrl) {
            this.retailStoreMainImageUrl = retailStoreMainImageUrl;
        }
    }

}
