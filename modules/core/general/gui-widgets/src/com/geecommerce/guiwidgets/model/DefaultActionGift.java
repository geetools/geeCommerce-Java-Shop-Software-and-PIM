package com.geecommerce.guiwidgets.model;

import com.google.common.collect.Maps;
import com.google.inject.Inject;
import com.geecommerce.catalog.product.model.Product;
import com.geecommerce.catalog.product.repository.Products;
import com.geecommerce.core.service.AbstractModel;
import com.geecommerce.core.service.annotation.Column;
import com.geecommerce.core.service.annotation.Model;
import com.geecommerce.core.type.ContextObject;
import com.geecommerce.core.type.Id;
import com.geecommerce.guiwidgets.enums.SlideType;
import com.geecommerce.mediaassets.model.MediaAsset;
import com.geecommerce.mediaassets.service.MediaAssetService;

import java.util.Date;
import java.util.Map;

@Model
public class DefaultActionGift extends AbstractModel implements ActionGift {

    @Column(Col.ID)
    private Id id = null;
    private MediaAsset mediaAsset = null;

    @Column(Col.NAME)
    private ContextObject<String> name = null;

    @Column(Col.DESCRIPTION)
    private ContextObject<String> description = null;

    @Column(Col.MEDIA_ASSET_ID)
    private Id mediaAssetId = null;

    @Column(Col.POSITION)
    private Integer position = null;

    @Column(Col.URL)
    private String url = null;

    private final MediaAssetService mediaAssetService;

    @Inject
    public DefaultActionGift(MediaAssetService mediaAssetService) {
	this.mediaAssetService = mediaAssetService;
    }

    @Override
    public ActionGift setId(Id id) {
	this.id = id;
	return this;
    }

    @Override
    public MediaAsset getMediaAsset() {
	if (mediaAsset == null && mediaAssetId != null)
	    mediaAsset = mediaAssetService.get(mediaAssetId);
	return mediaAsset;
    }

    @Override
    public ActionGift setMediaAsset(MediaAsset mediaAsset) {
	this.mediaAsset = mediaAsset;
	this.mediaAssetId = mediaAsset.getId();
	return this;
    }

    @Override
    public String getUrl() {
	if (url == null && mediaAssetId != null) {
	    MediaAsset asset = getMediaAsset();
	    url = asset.getUrl();
	}
	return url;
    }

    @Override
    public ActionGift setUrl(String url) {
	this.url = url;
	return this;
    }

    @Override
    public Integer getPosition() {
	return position;
    }

    @Override
    public ActionGift setPosition(Integer position) {
	this.position = position;
	return this;
    }

    @Override
    public ContextObject<String> getName() {
	return name;
    }

    @Override
    public ActionGift setName(ContextObject<String> name) {
	this.name = name;
	return this;
    }

    @Override
    public ContextObject<String> getDescription() {
	return description;
    }

    @Override
    public ActionGift setDescription(ContextObject<String> description) {
	this.description = description;
	return this;
    }

    @Override
    public void fromMap(Map<String, Object> map) {
	if (map == null)
	    return;

	super.fromMap(map);

	this.id = id_(map.get(Col.ID));
	this.mediaAssetId = id_(map.get(Col.MEDIA_ASSET_ID));
	this.position = int_(map.get(Col.POSITION));
	this.name = ctxObj_(map.get(Col.NAME));
	this.description = ctxObj_(map.get(Col.DESCRIPTION));
    }

    @Override
    public Map<String, Object> toMap() {
	Map<String, Object> map = Maps.newLinkedHashMap(super.toMap());
	map.put(Col.ID, getId());
	map.put(Col.MEDIA_ASSET_ID, mediaAssetId);
	map.put(Col.POSITION, getPosition());
	map.put(Col.NAME, getName());
	map.put(Col.DESCRIPTION, getDescription());

	return map;
    }

    @Override
    public Id getId() {
	return id;
    }
}
