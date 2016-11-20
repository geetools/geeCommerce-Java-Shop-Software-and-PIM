package com.geecommerce.cart.shipping;

import java.util.List;

import com.geecommerce.cart.model.Cart;
import com.geecommerce.core.service.api.Injectable;
import com.geecommerce.shipping.model.ShippingPackage;

public interface PackageSplitter extends Injectable {
    public List<ShippingPackage> toShippingPackages(Cart cart);
}
