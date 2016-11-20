package com.geecommerce.shipping.model;

import java.util.Date;

import com.geecommerce.core.service.AbstractModel;
import com.geecommerce.core.service.annotation.Model;
import com.geecommerce.core.type.Id;

@Model
public class DefaultShippingEvent extends AbstractModel implements ShippingEvent {
    private static final long serialVersionUID = -8826654921035918592L;
    private String name;
    private String location;
    private String country;
    private Date date;

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    @Override
    public Id getId() {
        return null; // To change body of implemented methods use File |
                     // Settings | File Templates.
    }
}
