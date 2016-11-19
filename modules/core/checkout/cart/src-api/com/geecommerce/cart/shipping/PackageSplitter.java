package com.geecommerce.cart.shipping;

import com.geecommerce.cart.model.Cart;
import com.geecommerce.core.service.api.Injectable;
import com.geecommerce.shipping.model.ShippingPackage;

import java.util.List;

public interface PackageSplitter extends Injectable {
    public List<ShippingPackage> toShippingPackages(Cart cart);
}
