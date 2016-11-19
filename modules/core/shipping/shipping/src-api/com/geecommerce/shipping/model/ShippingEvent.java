package com.geecommerce.shipping.model;

import com.geecommerce.core.service.api.Model;

import java.util.Date;

public interface ShippingEvent extends Model {

    public Date getDate();

    public void setDate(Date date);

    public String getName();

    public void setName(String name);

    public String getLocation();

    public void setLocation(String location);

    public String getCountry();

    public void setCountry(String country);
}
