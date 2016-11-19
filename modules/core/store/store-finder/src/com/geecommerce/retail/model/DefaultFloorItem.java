package com.geecommerce.retail.model;

import com.google.common.collect.Maps;
import com.geecommerce.core.service.AbstractAttributeSupport;
import com.geecommerce.core.service.annotation.Model;
import com.geecommerce.core.type.Id;

import java.util.Map;

import static com.geecommerce.retail.model.FloorItem.Col.*;

@Model
public class DefaultFloorItem extends AbstractAttributeSupport implements FloorItem {
    private Id id = null;
    private String name;
    private String description;
    private Integer number;
    private String imageUri;

    public Id getId() {
	return id;
    }

    public FloorItem setId(Id id) {
	this.id = id;
	return this;
    }

    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }

    public Integer getNumber() {
	return number;
    }

    public void setNumber(Integer number) {
	this.number = number;
    }

    public String getDescription() {
	return description;
    }

    public void setDescription(String description) {
	this.description = description;
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
	    this.name = str_(map.get(NAME));
	    this.description = str_(map.get(DESCRIPTION));
	    this.number = int_(map.get(NUMBER));
	    this.imageUri = str_(map.get(IMAGE_URI));
	}
    }

    @Override
    public Map<String, Object> toMap() {
	Map<String, Object> map = Maps.newLinkedHashMap(super.toMap());

	map.put(ID, getId());
	map.put(NAME, getName());
	map.put(DESCRIPTION, getDescription());
	map.put(NUMBER, getNumber());
	map.put(IMAGE_URI, getImageUri());

	return map;
    }
}
