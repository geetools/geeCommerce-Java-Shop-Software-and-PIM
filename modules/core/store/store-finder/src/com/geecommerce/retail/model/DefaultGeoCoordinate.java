package com.geecommerce.retail.model;

import com.google.common.collect.Maps;
import com.geecommerce.core.service.AbstractModel;
import com.geecommerce.core.service.annotation.Model;
import com.geecommerce.core.type.Id;
import java.util.Map;

import static com.geecommerce.retail.model.GeoCoordinate.Column.*;

@Model("geodb_coordinates")
public class DefaultGeoCoordinate extends AbstractModel implements GeoCoordinate {

    private static final long serialVersionUID = 133923683264098006L;

    private Id id = null;
    private Double latitude;
    private Double longitude;

    public Id getId() {
	return id;
    }

    public void setId(Id id) {
	this.id = id;
    }

    public Double getLatitude() {
	return latitude;
    }

    public void setLatitude(Double latitude) {
	this.latitude = latitude;
    }

    public Double getLongitude() {
	return longitude;
    }

    public void setLongitude(Double longitude) {
	this.longitude = longitude;
    }

    public void fromMap(Map<String, Object> map) {
	if (map != null) {
	    super.fromMap(map);

	    this.id = id_(map.get(ID));
	    this.latitude = double_(map.get(LATITUDE));
	    this.longitude = double_(map.get(LONGITUDE));
	}
    }

    public Map<String, Object> toMap() {
	Map<String, Object> map = Maps.newLinkedHashMap(super.toMap());

	map.put(ID, getId());
	map.put(LATITUDE, getLatitude());
	map.put(LONGITUDE, getLongitude());

	return map;
    }
}
