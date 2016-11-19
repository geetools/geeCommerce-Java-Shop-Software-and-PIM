package com.geecommerce.retail.model;

import com.google.common.collect.Maps;
import com.geecommerce.core.service.AbstractAttributeSupport;
import com.geecommerce.core.service.annotation.Model;
import com.geecommerce.core.type.Id;
import java.util.Map;

import static com.geecommerce.retail.model.ShoppingCenterInfo.Col.*;

@Model
public class DefaultShoppingCenterInfo extends AbstractAttributeSupport implements ShoppingCenterInfo {

    private Id id;
    private String text;
    private String webSite;
    private String webSiteLinkText;
    private String imageUri;

    public Id getId() {
	return id;
    }

    public ShoppingCenterInfo setId(Id id) {
	this.id = id;
	return this;
    }

    public String getText() {
	return text;
    }

    public void setText(String text) {
	this.text = text;
    }

    public String getWebSite() {
	return webSite;
    }

    public void setWebSite(String webSite) {
	this.webSite = webSite;
    }

    public String getWebSiteLinkText() {
	return webSiteLinkText;
    }

    public void setWebSiteLinkText(String webSiteLinkText) {
	this.webSiteLinkText = webSiteLinkText;
    }

    public String getImageUri() {
	return imageUri;
    }

    public void setImageUri(String imageUri) {
	this.imageUri = imageUri;
    }

    @Override
    public void fromMap(Map<String, Object> map) {
	if (map != null) {
	    super.fromMap(map);

	    this.id = id_(map.get(ID));
	    this.text = str_(map.get(TEXT));
	    this.webSite = str_(map.get(WEB_SITE));
	    this.webSiteLinkText = str_(map.get(WEB_SITE_LINK_TEXT));
	    this.imageUri = str_(map.get(IMAGE_URI));
	}
    }

    @Override
    public Map<String, Object> toMap() {
	Map<String, Object> map = Maps.newLinkedHashMap(super.toMap());

	map.put(ID, getId());
	map.put(TEXT, getText());
	map.put(WEB_SITE, getWebSite());
	map.put(WEB_SITE_LINK_TEXT, getWebSiteLinkText());
	map.put(IMAGE_URI, getImageUri());

	return map;
    }

}
