package com.geecommerce.retail.model;

import com.geecommerce.core.service.api.Model;
import com.geecommerce.core.type.Id;

import java.util.Set;

public interface GeoCoordinate extends Model {

    Id getId();

    void setId(Id id);

    Double getLatitude();

    void setLatitude(Double latitude);

    Double getLongitude();

    void setLongitude(Double longitude);

    class Column {
	public static final String ID = "loc_id";
	public static final String LATITUDE = "lat";
	public static final String LONGITUDE = "lon";
    }
}
