package com.geecommerce.shipping.model;

import java.util.List;

import com.geecommerce.core.service.api.Model;
import com.geecommerce.shipping.enums.ShippingType;

public interface ShippingPackage extends Model {

    public ShippingAddress getShippingAddress();

    public ShippingPackage setShippingAddress(ShippingAddress shippingAddress);

    public List<ShippingItem> getShippingItems();

    public ShippingPackage setShippingItems(List<ShippingItem> shippingItems);

    public ShippingPackage addShippingItem(ShippingItem shippingItem);

    public Double getTotalAmount();

    public ShippingPackage setTotalAmount(Double totalAmount);

    public ShippingType getType();

    public ShippingPackage setType(ShippingType type);

    public ShippingPackage setCalculateShipping(boolean calculateShipping);

    public boolean getCalculateShipping();

}
