package com.geecommerce.cart.service;

import com.geecommerce.cart.model.Cart;
import com.geecommerce.cart.model.CartItem;
import com.geecommerce.core.service.api.Service;
import com.geecommerce.core.type.Id;

public interface CartService extends Service {
    public Cart getCart(Id cartId);

    public Cart createCart(Cart cart);

    public void updateCart(Cart cart);

    public Cart getCartForCustomer(Id customerId);

    public Boolean isRetailStoreExist(String storeId);

    public void updatePickupAvailability(Cart cart, CartItem cartItem, String pickupStore, String zipCode, boolean isDeliveryCart);

    public void updateDeliveryAvailability(CartItem cartItem, String zipCode);
}
