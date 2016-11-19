package com.geecommerce.retail.controller;

import com.geecommerce.core.service.AbstractMultiContextModel;
import com.geecommerce.core.service.annotation.Cacheable;
import com.geecommerce.core.service.annotation.Model;
import com.geecommerce.core.service.api.MultiContextModel;
import com.geecommerce.core.type.Id;

import java.io.Serializable;
import java.util.List;

public class RetailStoreView {
    private Id id = null;
    private String id2 = null;
    private String retailStoreMainImageUrl = null;
    private String retailStoreMainImageUrlThumb = null;
    private String title = null;
    private String name = null;
    private List<String> addressLines = null;
    private String openingHours = null;
    private String specialOpeningTimes = null;
    private String telephone = null;
    private String telephoneShipping = null;
    private String telephoneFinancing = null;
    private String fax = null;
    private String email = null;
    private String mapImage = null;
    private String directions = null;
    private String restaurantMenu = null;
    private String geoLocation = null;
    private String mainImageUrl;
    private String distance = null;

    public Id getId() {
	return id;
    }

    public void setId(Id id) {
	this.id = id;
    }

    public String getId2() {
	return id2;
    }

    public void setId2(String id2) {
	this.id2 = id2;
    }

    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }

    public String getTelephoneShipping() {
	return telephoneShipping;
    }

    public void setTelephoneShipping(String telephoneShipping) {
	this.telephoneShipping = telephoneShipping;
    }

    public String getTelephoneFinancing() {
	return telephoneFinancing;
    }

    public void setTelephoneFinancing(String telephoneFinancing) {
	this.telephoneFinancing = telephoneFinancing;
    }

    public String getMapImage() {
	return mapImage;
    }

    public void setMapImage(String mapImage) {
	this.mapImage = mapImage;
    }

    public String getDirections() {
	return directions;
    }

    public void setDirections(String directions) {
	this.directions = directions;
    }

    public String getRestaurantMenu() {
	return restaurantMenu;
    }

    public void setRestaurantMenu(String restaurantMenu) {
	this.restaurantMenu = restaurantMenu;
    }

    public String getGeoLocation() {
	return geoLocation;
    }

    public void setGeoLocation(String geoLocation) {
	this.geoLocation = geoLocation;
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

    public String getSpecialOpeningTimes() {
	return specialOpeningTimes;
    }

    public void setSpecialOpeningTimes(String specialOpeningTimes) {
	this.specialOpeningTimes = specialOpeningTimes;
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

    public String getRetailStoreMainImageUrlThumb() {
	return retailStoreMainImageUrlThumb;
    }

    public void setRetailStoreMainImageUrlThumb(String retailStoreMainImageUrlThumb) {
	this.retailStoreMainImageUrlThumb = retailStoreMainImageUrlThumb;
    }

    public String getDistance() {
	return distance;
    }

    public void setDistance(String distance) {
	this.distance = distance;
    }

    public String getMainImageUrl() {
	return mainImageUrl;
    }

    public void setMainImageUrl(String mainImageUrl) {
	this.mainImageUrl = mainImageUrl;
    }
}
