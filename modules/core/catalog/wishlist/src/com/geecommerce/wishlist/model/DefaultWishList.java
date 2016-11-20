package com.geecommerce.wishlist.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.geecommerce.catalog.product.model.Product;
import com.geecommerce.core.service.AbstractModel;
import com.geecommerce.core.service.annotation.Model;
import com.geecommerce.core.system.model.RequestContext;
import com.geecommerce.core.type.Id;
import com.geecommerce.core.util.DateTimes;
import com.geecommerce.customer.model.Customer;
import com.google.common.collect.Lists;

@Model("wishlists")
public class DefaultWishList extends AbstractModel implements WishList {
    private static final long serialVersionUID = -8062542464897771454L;
    private Id id = null;
    private Id requestContextId = null;
    private Id customerId = null;
    private Date createdOn = null;
    private Date modifiedOn = null;
    private Boolean isDefault = null;
    private String name = null;
    private WishListAccessType accessType = null;
    private boolean clearItems = false;

    private List<WishListItem> wishListItems = new ArrayList<>();

    @Override
    public Id getId() {
        return id;
    }

    @Override
    public WishList setId(Id id) {
        this.id = id;
        return this;
    }

    @Override
    public Id getRequestContextId() {
        return requestContextId;
    }

    @Override
    public WishList fromRequestContext(RequestContext requestContext) {
        if (requestContext == null || requestContext.getId() == null)
            throw new IllegalStateException("RequestContext cannot be null");

        this.requestContextId = requestContext.getId();
        return this;
    }

    @Override
    public Id getCustomerId() {
        return customerId;
    }

    @Override
    public WishList belongsTo(Customer customer) {
        if (customer == null || customer.getId() == null)
            throw new IllegalStateException("Customer cannot be null");

        this.customerId = customer.getId();
        return this;
    }

    @Override
    public Date getCreatedOn() {
        return createdOn;
    }

    @Override
    public Date getModifiedOn() {
        return modifiedOn;
    }

    @Override
    public List<WishListItem> getWishListItems() {
        if (wishListItems == null)
            wishListItems = new ArrayList<>();
        return wishListItems;
    }

    @Override
    public WishList addProduct(Product product) {
        if (product == null || product.getId() == null)
            return this;

        WishListItem wishListItem = findItem(product.getId());

        if (wishListItem == null) {
            wishListItem = app.model(WishListItem.class).setProduct(product);
            wishListItem.setType(WishListItemType.PRODUCT);
            wishListItem.setId(app.nextId());
            wishListItem.setCreatedOn(DateTimes.newDate());

            wishListItems.add(wishListItem);
        }

        return this;
    }

    @Override
    public WishList addIdea(String idea) {
        WishListItem wishListItem = app.model(WishListItem.class);
        wishListItem.setType(WishListItemType.IDEA);
        wishListItem.setIdea(idea);
        wishListItem.setId(app.nextId());
        wishListItems.add(wishListItem);
        wishListItem.setCreatedOn(DateTimes.newDate());
        return this;
    }

    @Override
    public WishList removeWishListItem(Id wishListItemId) {
        if (wishListItemId == null)
            return this;

        List<WishListItem> wishListItems = getWishListItems();

        if (wishListItems != null && wishListItems.size() > 0) {
            for (int i = 0; i < wishListItems.size(); i++) {
                if (wishListItemId.equals(wishListItems.get(i).getId())) {
                    wishListItems.remove(i);
                    clearItems = true;
                }
            }
        }

        return this;
    }

    protected WishListItem findItem(Id productId) {
        for (WishListItem item : wishListItems) {
            if (item.getType().equals(WishListItemType.PRODUCT) && item.getProductId().equals(productId)) {
                return item;
            }
        }

        return null;
    }

    @Override
    public Boolean getDefault() {
        return isDefault;
    }

    @Override
    public WishList setDefault(Boolean isDefault) {
        this.isDefault = isDefault;
        return this;
    }

    @Override
    public WishListAccessType getAccessType() {
        return accessType;
    }

    @Override
    public WishList setAccessType(WishListAccessType accessType) {
        this.accessType = accessType;
        return this;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public WishList setName(String name) {
        this.name = name;
        return this;
    }

    @Override
    public void fromMap(Map<String, Object> map) {
        this.id = id_(map.get(Column.ID));
        this.requestContextId = id_(map.get(Column.REQUEST_CONTEXT_ID));
        this.customerId = id_(map.get(Column.CUSTOMER_ID));
        this.isDefault = bool_(map.get(Column.DEFAULT));
        this.name = str_(map.get(Column.NAME));
        this.accessType = WishListAccessType.valueOf(str_(map.get(Column.ACCESS_TYPE)));

        List<Map<String, Object>> items = list_(map.get(Column.WISHLIST_ITEMS));

        if (items != null && items.size() > 0) {
            for (Map<String, Object> item : items) {
                WishListItem wishListItem = app.model(WishListItem.class);
                wishListItem.fromMap(item);
                this.wishListItems.add(wishListItem);
            }
        }
    }

    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put(Column.ID, getId());
        m.put(Column.REQUEST_CONTEXT_ID, getRequestContextId());

        if (getCustomerId() != null) {
            m.put(Column.CUSTOMER_ID, getCustomerId());
        }

        m.put(Column.DEFAULT, getDefault());
        m.put(Column.NAME, getName());
        m.put(Column.ACCESS_TYPE, getAccessType().toString());

        if (!wishListItems.isEmpty()) {
            List<Map<String, Object>> l = new ArrayList<>();

            for (WishListItem wishListItem : wishListItems) {
                l.add(wishListItem.toMap());
            }

            m.put(Column.WISHLIST_ITEMS, l);
        } else if (clearItems) {
            m.put(Column.WISHLIST_ITEMS, Lists.newArrayList());
            clearItems = false;
        }

        return m;
    }

}
