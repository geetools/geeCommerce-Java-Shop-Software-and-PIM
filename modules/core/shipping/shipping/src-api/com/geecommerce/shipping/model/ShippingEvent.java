package com.geecommerce.shipping.model;

import java.util.Date;

import com.geecommerce.core.service.api.Model;

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
