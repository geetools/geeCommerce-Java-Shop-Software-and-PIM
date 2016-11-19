package com.geecommerce.cart.helper;

import com.geecommerce.cart.model.Cart;
import com.geecommerce.core.service.api.Helper;

public interface CartHelper extends Helper {

    public Cart getCart();

    public Cart getCart(boolean createIfNotExists);

}
