package com.geecommerce.retail.controller;

import java.util.LinkedList;
import java.util.List;

import com.geecommerce.core.type.Id;
import com.geecommerce.mediaassets.model.MediaAsset;
import com.geecommerce.mediaassets.service.MediaAssetService;
import com.geecommerce.retail.model.RetailStore;
import com.geecommerce.retail.service.LocationService;

/**
 * Created by Andrey on 23.09.2015.
 */
public class RetailStoreViewBuilder {

    private final MediaAssetService mediaAssetService;
    private final LocationService locationService;

    private RetailStoreView retailStoreView = new RetailStoreView();

    public RetailStoreViewBuilder(MediaAssetService mediaAssetService, LocationService locationService) {
        this.mediaAssetService = mediaAssetService;
        this.locationService = locationService;
    }

    public List<RetailStoreView> createRetailStoreViews(List<RetailStore> retailStores) {
        List<RetailStoreView> retailStoreViews = new LinkedList();
        retailStores.forEach(retailStore -> retailStoreViews.add(createRetailStoreView(retailStore).build()));
        return retailStoreViews;
    }

    public List<RetailStoreView> createRetailStoreViewWithDistances(List<RetailStore> retailStores,
        String customerZipCode) {
        List<RetailStoreView> retailStoreViews = new LinkedList();
        retailStores
            .forEach(
                retailStore -> retailStoreViews
                    .add(createRetailStoreView(retailStore)
                        .calculateDistance(
                            new LocationService.Location(retailStore.attr("latitude").getDouble(),
                                retailStore.attr("longitude").getDouble()),
                            customerZipCode)
                        .build()));
        return retailStoreViews;
    }

    public RetailStoreViewBuilder createRetailStoreView(RetailStore retailStore) {
        createImage(retailStore.getMainImageId());
        retailStoreView.setId(retailStore.getId());
        retailStoreView.setId2(retailStore.getId2());
        retailStoreView.setTitle(retailStore.getTitle());
        retailStoreView.setName(retailStore.getName());
        retailStoreView.setAddressLines(retailStore.getAddressLines());
        retailStoreView.setMainImageUrl(retailStore.getMainImageUri());

        if (retailStore.attr("opening_hours") != null) {
            retailStoreView.setOpeningHours(retailStore.attr("opening_hours").getStr());
        }
        if (retailStore.attr("special_opening_times") != null) {
            retailStoreView.setSpecialOpeningTimes(retailStore.attr("special_opening_times").getStr());
        }
        retailStoreView.setTelephone(retailStore.getTelephone());
        if (retailStore.attr("tel_number_shipping") != null) {
            retailStoreView.setTelephoneShipping(retailStore.attr("tel_number_shipping").getStr());
        }
        if (retailStore.attr("tel_number_financing") != null) {
            retailStoreView.setTelephoneFinancing(retailStore.attr("tel_number_financing").getStr());
        }
        retailStoreView.setFax(retailStore.getFax());
        retailStoreView.setEmail(retailStore.getEmail());
        if (retailStore.attr("map_image_html") != null) {
            retailStoreView.setMapImage(retailStore.attr("map_image_html").getStr());
        }
        if (retailStore.attr("directions") != null) {
            retailStoreView.setDirections(retailStore.attr("directions").getStr());
        }
        if (retailStore.attr("geo_location") != null) {
            retailStoreView.setGeoLocation(retailStore.attr("geo_location").getStr());
        }
        if (retailStore.attr("restaurant_menu") != null) {
            retailStoreView.setRestaurantMenu(retailStore.attr("restaurant_menu").getStr());
        }
        return this;
    }

    public RetailStoreViewBuilder createImage(Id mainImageId) {
        if (mainImageId != null) {
            MediaAsset mediaAsset = mediaAssetService.get(mainImageId);
            if (mediaAsset != null) {
                retailStoreView.setRetailStoreMainImageUrl(mediaAsset.getUrl(640, 480));
            }
            retailStoreView.setRetailStoreMainImageUrlThumb(mediaAsset.getUrl(200, 190));
        }
        return this;
    }

    public RetailStoreViewBuilder calculateDistance(LocationService.Location storeLocation, String customerZipCode) { // TODO
        Double distance = locationService.distance(customerZipCode, storeLocation);
        retailStoreView.setDistance(distance.toString());
        return this;
    }

    public RetailStoreView build() {
        return retailStoreView;
    }
}
