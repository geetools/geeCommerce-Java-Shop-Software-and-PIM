package com.geecommerce.core.system.pojo;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;

import com.geecommerce.core.enums.PermissionAction;
import com.geecommerce.core.type.Id;

@XmlRootElement(name = "context_value")
public class ContextValue implements Serializable {
    private static final long serialVersionUID = 8984045720577755915L;

    private PermissionAction action = null;

    private int hashCode = 0;

    private Id merchantId = null;

    private Id storeId = null;

    private String country = null;

    private String language = null;

    private Id viewId = null;

    private Object value = null;

    public PermissionAction getAction() {
	return action;
    }

    public void setAction(PermissionAction action) {
	this.action = action;
    }

    public int getHashCode() {
	return hashCode;
    }

    public void setHashCode(int hashCode) {
	this.hashCode = hashCode;
    }

    public Id getMerchantId() {
	return merchantId;
    }

    public void setMerchantId(Id merchantId) {
	this.merchantId = merchantId;
    }

    public Id getStoreId() {
	return storeId;
    }

    public void setStoreId(Id storeId) {
	this.storeId = storeId;
    }

    public String getCountry() {
	return country;
    }

    public void setCountry(String country) {
	this.country = country;
    }

    public String getLanguage() {
	return language;
    }

    public void setLanguage(String language) {
	this.language = language;
    }

    public Id getViewId() {
	return viewId;
    }

    public void setViewId(Id viewId) {
	this.viewId = viewId;
    }

    public Object getValue() {
	return value;
    }

    public void setValue(Object value) {
	this.value = value;
    }
}
