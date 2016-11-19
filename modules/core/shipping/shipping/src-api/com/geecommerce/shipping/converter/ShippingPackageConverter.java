package com.geecommerce.shipping.converter;

import com.geecommerce.shipping.model.ShippingPackage;

import java.util.List;

public interface ShippingPackageConverter {
    public List<ShippingPackage> toShippingPackages();
}
