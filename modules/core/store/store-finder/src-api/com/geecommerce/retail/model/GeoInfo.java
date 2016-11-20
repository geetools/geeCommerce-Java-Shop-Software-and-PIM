package com.geecommerce.retail.model;

import com.geecommerce.core.service.api.Model;
import com.geecommerce.core.type.Id;

public interface GeoInfo extends Model {

    Id getId();

    void setId(Id id);

    String getZip();

    void setZip(String zip);

    Double getLatitude();

    void setLatitude(Double latitude);

    Double getLongitude();

    void setLongitude(Double longitude);

    class Col {
        public static final String ID = "id";
        public static final String LATITUDE = "latitude";
        public static final String LONGITUDE = "longitude";
        public static final String ZIP = "zip";
    }
}
