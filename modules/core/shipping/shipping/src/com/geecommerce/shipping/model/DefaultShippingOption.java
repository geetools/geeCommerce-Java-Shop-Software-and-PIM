package com.geecommerce.shipping.model;

import com.geecommerce.core.service.annotation.Injectable;

@Injectable
public class DefaultShippingOption implements ShippingOption {
    private static final long serialVersionUID = -739197129462668405L;
    private String name;
    private String description;
    private Double rate;
    private String carrierCode;
    private String optionCode;
    private String group;
    private ShippingPackage shippingPackage;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getRate() {
        return rate;
    }

    public void setRate(Double rate) {
        this.rate = rate;
    }

    public String getCarrierCode() {
        return carrierCode;
    }

    public void setCarrierCode(String carrierCode) {
        this.carrierCode = carrierCode;
    }

    public String getOptionCode() {
        return optionCode;
    }

    public void setOptionCode(String optionCode) {
        this.optionCode = optionCode;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    @Override
    public ShippingPackage getShippingPackage() {
        return shippingPackage;
    }

    @Override
    public void setShippingPackage(ShippingPackage shippingPackage) {
        this.shippingPackage = shippingPackage;
    }
}
