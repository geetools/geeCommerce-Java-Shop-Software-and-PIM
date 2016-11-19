package com.geecommerce.retail.model;

import java.util.Map;

import com.google.common.collect.Maps;
import com.geecommerce.core.service.AbstractMultiContextModel;
import com.geecommerce.core.service.annotation.Model;
import com.geecommerce.core.type.Id;

@Model("retail_store_distances")
public class DefaultRetailStoreDistance extends AbstractMultiContextModel implements RetailStoreDistance {
    private static final long serialVersionUID = 1L;
    private Id id = null;
    private String fromZip = null;
    private String fromCity = null;
    private Id toRetailStore = null;
    private Integer distance = null;

    @Override
    public Id getId() {
	return id;
    }

    @Override
    public RetailStoreDistance setId(Id id) {
	this.id = id;
	return this;
    }

    @Override
    public String getFromZip() {
	return fromZip;
    }

    @Override
    public RetailStoreDistance setFromZip(String fromZip) {
	this.fromZip = fromZip;
	return this;
    }

    @Override
    public String getFromCity() {
	return fromCity;
    }

    @Override
    public RetailStoreDistance setFromCity(String fromCity) {
	this.fromCity = fromCity;
	return this;
    }

    @Override
    public Id getToRetailStore() {
	return toRetailStore;
    }

    @Override
    public RetailStoreDistance setToRetailStore(Id toRetailStore) {
	this.toRetailStore = toRetailStore;
	return this;
    }

    @Override
    public Integer getDistance() {
	return distance;
    }

    @Override
    public RetailStoreDistance setDistance(Integer distance) {
	this.distance = distance;
	return this;
    }

    public void fromMap(Map<String, Object> map) {
	if (map == null)
	    return;

	super.fromMap(map);

	this.id = id_(map.get(Column.ID));
	this.fromZip = str_(map.get(Column.FROM_ZIP));
	this.fromCity = str_(map.get(Column.FROM_CITY));
	this.toRetailStore = id_(map.get(Column.TO_RETAIL_STORE));
	this.distance = int_(map.get(Column.DISTANCE));
    }

    @Override
    public Map<String, Object> toMap() {
	Map<String, Object> map = Maps.newLinkedHashMap(super.toMap());

	map.put(Column.ID, getId());
	map.put(Column.FROM_ZIP, getFromZip());
	map.put(Column.FROM_CITY, getFromCity());
	map.put(Column.TO_RETAIL_STORE, getToRetailStore());
	map.put(Column.DISTANCE, getDistance());

	return map;
    }
}
