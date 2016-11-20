package com.geecommerce.cart.service;

import java.util.HashMap;
import java.util.Map;

import com.geecommerce.cart.model.Cart;
import com.geecommerce.cart.model.CartItem;
import com.geecommerce.cart.repository.Carts;
import com.geecommerce.core.service.annotation.Service;
import com.geecommerce.core.type.Id;
import com.google.inject.Inject;

@Service
public class DefaultCartService implements CartService {
    private final Carts carts;

    @Inject
    public DefaultCartService(Carts carts) {
        this.carts = carts;
    }

    @Override
    public Cart createCart(Cart cart) {
        return carts.add(cart);
    }

    @Override
    public void updateCart(Cart cart) {
        carts.update(cart);
    }

    @Override
    public Cart getCart(Id id) {
        return carts.findById(Cart.class, id);
    }

    @Override
    public Cart getCartForCustomer(Id customerId) {

        Map<String, Object> filter = new HashMap<>();
        filter.put(Cart.Column.CUSTOMER_ID, customerId);
        filter.put(Cart.Column.ENABLED, true);
        return carts.findOne(Cart.class, filter);
    }

    @Override
    public Boolean isRetailStoreExist(String storeId) {
        return true;
    }

    @Override
    public void updateDeliveryAvailability(CartItem cartItem, String zipCode) {
        // no implementation
    }

    @Override
    public void updatePickupAvailability(Cart cart, CartItem cartItem, String pickupStore, String zipCode,
        boolean isDeliveryCart) {
        // no implementation
    }
}