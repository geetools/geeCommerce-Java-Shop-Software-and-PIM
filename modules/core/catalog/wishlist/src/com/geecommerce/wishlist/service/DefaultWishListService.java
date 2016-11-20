package com.geecommerce.wishlist.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.geecommerce.core.service.annotation.Service;
import com.geecommerce.core.type.Id;
import com.geecommerce.wishlist.model.WishList;
import com.geecommerce.wishlist.repository.WishLists;
import com.google.inject.Inject;

@Service
public class DefaultWishListService implements WishListService {
    private final WishLists wishLists;

    @Inject
    public DefaultWishListService(WishLists wishLists) {
        this.wishLists = wishLists;
    }

    @Override
    public WishList getDefaultWishList(Id customerId) {
        Map<String, Object> filter = new HashMap<>();
        filter.put(WishList.Column.CUSTOMER_ID, customerId);
        filter.put(WishList.Column.DEFAULT, true);
        return wishLists.findOne(WishList.class, filter);
    }

    @Override
    public List<WishList> getWishLists(Id customerId) {
        Map<String, Object> filter = new HashMap<>();
        filter.put(WishList.Column.CUSTOMER_ID, customerId);
        return wishLists.find(WishList.class, filter);
    }

    @Override
    public WishList getWishList(Id id) {
        return wishLists.findById(WishList.class, id);
    }

    @Override
    public WishList createWishList(WishList wishList) {
        return wishLists.add(wishList);
    }

    @Override
    public void updateWishList(WishList wishList) {
        wishLists.update(wishList);
    }

    @Override
    public void deleteWishList(WishList wishList) {
        wishLists.remove(wishList);
    }
}
