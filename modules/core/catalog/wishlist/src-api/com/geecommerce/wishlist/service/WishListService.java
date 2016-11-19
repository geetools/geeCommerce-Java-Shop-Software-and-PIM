package com.geecommerce.wishlist.service;

import java.util.List;

import com.geecommerce.core.service.api.Service;
import com.geecommerce.core.type.Id;
import com.geecommerce.wishlist.model.WishList;

public interface WishListService extends Service {
    public WishList getDefaultWishList(Id customerId);

    public List<WishList> getWishLists(Id customerId);

    public WishList getWishList(Id id);

    public WishList createWishList(WishList wishList);

    public void updateWishList(WishList wishList);

    public void deleteWishList(WishList wishList);
}
