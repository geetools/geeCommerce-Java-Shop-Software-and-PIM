package com.geecommerce.catalog.product.model;

import com.geecommerce.core.service.AbstractMultiContextModel;
import com.geecommerce.core.service.annotation.Cacheable;
import com.geecommerce.core.service.annotation.Column;
import com.geecommerce.core.service.annotation.Model;
import com.geecommerce.core.type.ContextObject;
import com.geecommerce.core.type.Id;

@Cacheable
@Model("catalog_media_types")
public class DefaultCatalogMediaType extends AbstractMultiContextModel implements CatalogMediaType {
    private static final long serialVersionUID = -6511881284495945282L;

    @Column(Col.ID)
    private Id id = null;

    @Column(Col.KEY)
    private String key = null;

    @Column(Col.LABEL)
    private ContextObject<String> label = null;

    @Column(Col.PRIORITY)
    private int priority = 99;

    @Override
    public Id getId() {
	return id;
    }

    @Override
    public CatalogMediaType setId(Id id) {
	this.id = id;
	return this;
    }

    @Override
    public String getKey() {
	return key;
    }

    @Override
    public CatalogMediaType setKey(String key) {
	this.key = key;
	return this;
    }

    @Override
    public ContextObject<String> getLabel() {
	return label;
    }

    @Override
    public CatalogMediaType setLabel(ContextObject<String> label) {
	this.label = label;
	return this;
    }

    @Override
    public int getPriority() {
	return priority;
    }

    @Override
    public CatalogMediaType setPriority(int priority) {
	this.priority = priority;
	return this;
    }
}
