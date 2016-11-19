package com.geecommerce.wishlist.model;

import com.geecommerce.core.type.Id;

public class WishListJson {
    private Id id;
    private String name;
    private Boolean isDefault;
    private Boolean isDelete;
    private String access;

    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }

    public Boolean getDefault() {
	return isDefault;
    }

    public void setDefault(Boolean isDefault) {
	this.isDefault = isDefault;
    }

    public Boolean getDelete() {
	return isDelete;
    }

    public void setDelete(Boolean delete) {
	isDelete = delete;
    }

    public String getAccess() {
	return access;
    }

    public void setAccess(String access) {
	this.access = access;
    }

    public Id getId() {
	return id;
    }

    public void setId(Id id) {
	this.id = id;
    }
}