package com.geecommerce.shipping.model;

import com.geecommerce.core.service.AbstractModel;
import com.geecommerce.core.service.annotation.Model;
import com.geecommerce.core.type.Id;

@Model
public class DefaultShippingAddress extends AbstractModel implements ShippingAddress {
    private static final long serialVersionUID = -5339693354331610451L;
    private String zip;
    private String state;
    private String country;

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
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
