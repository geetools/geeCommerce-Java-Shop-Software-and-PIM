package com.geecommerce.shipping.model;

import com.geecommerce.core.service.api.Model;

public interface ShippingAddress extends Model {

    public String getZip();

    public void setZip(String zip);

    public String getState();

    public void setState(String state);

    public String getCountry();

    public void setCountry(String country);
}
