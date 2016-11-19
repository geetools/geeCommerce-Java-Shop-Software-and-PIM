package com.geecommerce.shipping.model;

import com.geecommerce.core.service.api.Injectable;

public interface ShippingOption extends Injectable {
    public String getName();

    public void setName(String name);

    public String getDescription();

    public void setDescription(String description);

    public Double getRate();

    public void setRate(Double rate);

    public String getCarrierCode();

    public void setCarrierCode(String carrierCode);

    public String getOptionCode();

    public void setOptionCode(String optionCode);

    public String getGroup();

    public void setGroup(String name);

    public ShippingPackage getShippingPackage();

    public void setShippingPackage(ShippingPackage shippingPackage);
}
