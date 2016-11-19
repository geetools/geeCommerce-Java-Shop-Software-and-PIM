package com.geecommerce.wishlist.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.geecommerce.catalog.product.model.Product;
import com.geecommerce.catalog.product.service.ProductService;
import com.geecommerce.core.type.Id;
import com.geecommerce.core.util.Json;
import com.geecommerce.core.web.BaseActionBean;
import com.geecommerce.customer.model.Customer;
import com.geecommerce.wishlist.converter.WishListConverter;
import com.geecommerce.wishlist.model.WishList;
import com.geecommerce.wishlist.model.WishListAccessType;
import com.geecommerce.wishlist.model.WishListItem;
import com.geecommerce.wishlist.model.WishListJson;
import com.geecommerce.wishlist.service.WishListService;
import com.google.common.collect.Lists;
import com.google.inject.Inject;

import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.HandlesEvent;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.validation.Validate;

@UrlBinding("/wishlist/{$event}/{id}")
public class WishListAction extends BaseActionBean {

    private final WishListService wishListService;
    private final ProductService productService;

    private String idea = null;
    private Id productId = null;

    @Validate(required = false, converter = WishListConverter.class)
    private WishListJson[] wishListsJson = null;
    @Validate(required = false, converter = WishListConverter.class)
    private WishListJson wishListJson = null;

    private Id wishListId = null;
    private Id wishListItemId = null;
    private Id wishListToId = null;

    @Inject
    public WishListAction(WishListService wishListService, ProductService productService) {
        this.wishListService = wishListService;
        this.productService = productService;
    }

    @HandlesEvent("process-login")
    public Resolution processLogin() {

        if (isCustomerLoggedIn()) {
            return redirect("/wishlist/view/");
        }

        return new ForwardResolution("/customer/account/process-login/").addParameter("postLoginRedirect", "/wishlist/view/");
    }

    @HandlesEvent("add-product")
    public Resolution addProduct() {
        if (isCustomerLoggedIn()) {
            WishList wishList = getWishList(true);
            if (wishList != null) {
                Product p = productService.getProduct(productId);
                wishList.addProduct(p);
                wishListService.updateWishList(wishList);
                String url = "/wishlist/view/";
                if (getId() != null)
                    url += getId();

                return redirect(url);
            } else {
                // error
                return view("error");
            }
        } else {
            // redirect to login page;
            return redirect("/customer/account/login");
        }
    }

    @HandlesEvent("add-idea")
    public Resolution addIdea() {
        HashMap<String, String> result = new HashMap<>();

        if (isCustomerLoggedIn()) {
            WishList wishList = getWishList(true);
            if (wishList != null) {
                wishList.addIdea(getIdea());
                wishListService.updateWishList(wishList);
                result.put("status", "ok");
                return json(Json.toJson(result));
            } else {
                // error
                result.put("status", "error");
                result.put("status", "Wishlist not found");
                return json(Json.toJson(result));
            }
        } else {
            // redirect to login page;
            result.put("status", "error");
            result.put("status", "User not logged in");
            return json(Json.toJson(result));
        }

    }

    @HandlesEvent("view")
    public Resolution view() {
        WishList wishList = getWishList(true);
        if (wishList == null) {
            addValidationError(app.message("Wish List doesn't exists"));
            return view("wishlist/view");
        }
        if (!isCustomerLoggedIn() && wishList.getAccessType().equals(WishListAccessType.PRIVATE)) {
            addValidationError(app.message("You can't access wishlist."));
        }
        return view("wishlist/view");
    }

    @HandlesEvent("create-wishlist")
    public Resolution createWishList() {
        if (isCustomerLoggedIn()) {
            if (getWishListJson() != null) {
                WishList wishList = app.getModel(WishList.class);
                wishList.setDefault(false);
                wishList.setName(getWishListJson().getName());
                wishList.setAccessType(WishListAccessType.valueOf(getWishListJson().getAccess()));
                wishList.belongsTo((Customer) getLoggedInCustomer());
                wishListService.createWishList(wishList);
            }
        }

        return json("");
    }

    @HandlesEvent("manage-wishlists")
    public Resolution manageWishLists() {
        WishListJson[] wishLists = getWishListsJson();
        WishList defaultWishListJson = null;
        boolean doneWithDefault = false;
        if (wishLists != null && wishLists.length > 0) {
            for (WishListJson wishListJson : wishLists) {
                if (wishListJson.getDelete()) {
                    WishList wishList = wishListService.getWishList(wishListJson.getId());
                    if (wishList != null) {
                        wishListService.deleteWishList(wishList);
                    }
                } else {
                    WishList wishList = wishListService.getWishList(wishListJson.getId());
                    if (wishListJson.getDefault()) {
                        defaultWishListJson = wishList;
                        if (wishList.getDefault())
                            doneWithDefault = true;
                    }
                    wishList.setAccessType(WishListAccessType.valueOf(wishListJson.getAccess()));
                    wishListService.updateWishList(wishList);
                }
            }
            if (!doneWithDefault) {
                WishList defaultWishList = getDefaultWishList(false);
                if (defaultWishList != null && defaultWishListJson != null) {
                    defaultWishList.setDefault(false);
                    wishListService.updateWishList(defaultWishList);
                    defaultWishListJson.setDefault(true);
                    wishListService.updateWishList(defaultWishListJson);
                } else if (defaultWishListJson != null) {
                    defaultWishListJson.setDefault(true);
                    wishListService.updateWishList(defaultWishListJson);
                }
            }
        }
        HashMap<String, String> result = new HashMap<>();
        result.put("status", "ok");
        return json(Json.toJson(result));
    }

    @HandlesEvent("move-wishlist-item")
    public Resolution moveWishListItem() {
        HashMap<String, String> result = new HashMap<>();
        boolean deleted = false;
        if (getWishListId() != null && getWishListItemId() != null && getWishListToId() != null) {
            WishList wishList = wishListService.getWishList(getWishListId());
            WishList wishListTo = wishListService.getWishList(getWishListToId());
            if (wishList != null && wishList.getWishListItems() != null && wishList.getWishListItems().size() > 0 && wishListTo != null) {
                for (int i = 0; i < wishList.getWishListItems().size(); i++) {
                    if (wishList.getWishListItems().get(i).getId().equals(getWishListItemId())) {
                        WishListItem item = wishList.getWishListItems().get(i);
                        deleted = true;
                        wishList.getWishListItems().remove(i);
                        wishListService.updateWishList(wishList);

                        item.setCreatedOn(new Date());
                        wishListTo.getWishListItems().add(item);
                        wishListService.updateWishList(wishListTo);
                        result.put("status", "ok");
                        return json(Json.toJson(result));
                    }
                }
            }
        }
        result.put("status", "error");
        result.put("error", "Item not found");
        return json(Json.toJson(result));
    }

    @HandlesEvent("delete-wishlist-item")
    public Resolution deleteWishListItem() {
        HashMap<String, String> result = new HashMap<>();

        if (getWishListId() != null && getWishListItemId() != null) {
            WishList wishList = wishListService.getWishList(getWishListId());
            wishList.removeWishListItem(getWishListItemId());

            wishListService.updateWishList(wishList);

            result.put("status", "ok");

            return json(Json.toJson(result));
        }

        result.put("status", "error");
        result.put("error", "Item not found");

        return json(Json.toJson(result));
    }

    @HandlesEvent("wishlists-json")
    public Resolution wishListsAsJson() {
        List<WishList> wishLists = getWishLists();
        if (wishLists != null) {
            List<WishListJson> wishListsJson = new ArrayList<>();
            for (WishList wishList : wishLists) {
                WishListJson wishListJson = new WishListJson();
                wishListJson.setId(wishList.getId());
                wishListJson.setDefault(wishList.getDefault());
                wishListJson.setName(wishList.getName());
                wishListJson.setDelete(false);
                wishListJson.setAccess(wishList.getAccessType().toString());
                wishListsJson.add(wishListJson);
            }
            return json(Json.toJson(wishListsJson));
        }

        return json("");
    }

    public WishList getWishList() {
        return getWishList(true);
    }

    public List<WishList> getWishLists() {
        if (isCustomerLoggedIn()) {
            Customer customer = getLoggedInCustomer();
            return wishListService.getWishLists(customer.getId());
        }
        return null;
    }

    public Integer getWishListsSize() {
        List<WishList> wishLists = getWishLists();
        if (wishLists != null) {
            return wishLists.size();
        }
        return 0;
    }

    public List<WishListItem> getWishListItems() {
        WishList wishList = getWishList();
        if (wishList == null) {
            return null;
        }
        return Lists.reverse(wishList.getWishListItems());
    }

    public WishList getWishList(boolean createIfNotExists) {
        Id wishListId = getId();
        WishList wishList = null;

        if (wishListId == null) {
            if (isCustomerLoggedIn()) {
                wishList = getDefaultWishList(createIfNotExists);
            }
        } else {
            wishList = wishListService.getWishList(wishListId);
        }

        return wishList;
    }

    public WishList getDefaultWishList(boolean createIfNotExists) {
        if (isCustomerLoggedIn()) {
            Customer customer = getLoggedInCustomer();
            WishList defaultWishList = wishListService.getDefaultWishList(customer.getId());
            if (defaultWishList == null && createIfNotExists) {
                WishList wishList = app.getModel(WishList.class);
                wishList.belongsTo(customer);
                wishList.setDefault(true);
                wishList.setName("Default Wish List");
                wishList.setAccessType(WishListAccessType.PRIVATE);
                return wishListService.createWishList(wishList);
            }
            return defaultWishList;
        }
        return null;
    }

    public String getIdea() {
        return idea;
    }

    public void setIdea(String idea) {
        this.idea = idea;
    }

    public Id getProductId() {
        return productId;
    }

    public void setProductId(Id productId) {
        this.productId = productId;
    }

    public WishListJson[] getWishListsJson() {
        return wishListsJson;
    }

    public void setWishListsJson(WishListJson[] wishListsJson) {
        this.wishListsJson = wishListsJson;
    }

    public WishListJson getWishListJson() {
        return wishListJson;
    }

    public void setWishListJson(WishListJson wishListJson) {
        this.wishListJson = wishListJson;
    }

    public Id getWishListId() {
        return wishListId;
    }

    public void setWishListId(Id wishListId) {
        this.wishListId = wishListId;
    }

    public Id getWishListItemId() {
        return wishListItemId;
    }

    public void setWishListItemId(Id wishListItemId) {
        this.wishListItemId = wishListItemId;
    }

    public Id getWishListToId() {
        return wishListToId;
    }

    public void setWishListToId(Id wishListToId) {
        this.wishListToId = wishListToId;
    }
}
