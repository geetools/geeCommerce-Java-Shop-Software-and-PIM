package com.geecommerce.shipping.converter;

import java.util.List;

import com.geecommerce.shipping.model.ShippingPackage;

public interface ShippingPackageConverter {
    public List<ShippingPackage> toShippingPackages();
}
