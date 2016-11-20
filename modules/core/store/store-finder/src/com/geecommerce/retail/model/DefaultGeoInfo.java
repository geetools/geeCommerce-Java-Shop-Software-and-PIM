package com.geecommerce.retail.model;

import static com.geecommerce.retail.model.GeoInfo.Col.ID;
import static com.geecommerce.retail.model.GeoInfo.Col.LATITUDE;
import static com.geecommerce.retail.model.GeoInfo.Col.LONGITUDE;
import static com.geecommerce.retail.model.GeoInfo.Col.ZIP;

import com.geecommerce.core.service.AbstractModel;
import com.geecommerce.core.service.annotation.Column;
import com.geecommerce.core.service.annotation.Model;
import com.geecommerce.core.type.Id;

@Model("zip_to_coordinates")
public class DefaultGeoInfo extends AbstractModel implements GeoInfo {

    private static final long serialVersionUID = 133923683264098006L;

    @Column(ID)
    private Id id;
    @Column(ZIP)
    private String zip;
    @Column(LATITUDE)
    private Double latitude;
    @Column(LONGITUDE)
    private Double longitude;

    public Id getId() {
        return id;
    }

    public void setId(Id id) {
        this.id = id;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
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
}
