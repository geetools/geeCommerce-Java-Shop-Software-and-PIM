package com.geecommerce.wishlist.model;

import java.util.List;

import com.geecommerce.catalog.product.model.Product;
import com.geecommerce.core.service.api.Model;
import com.geecommerce.core.system.model.RequestContext;
import com.geecommerce.core.type.Id;
import com.geecommerce.customer.model.Customer;

public interface WishList extends Model {
    public Id getId();

    public WishList setId(Id id);

    public Id getRequestContextId();

    public WishList fromRequestContext(RequestContext requestContext);

    public Id getCustomerId();

    public WishList belongsTo(Customer customer);

    public List<WishListItem> getWishListItems();

    public WishList addProduct(Product product);

    public WishList addIdea(String wish);

    public WishList removeWishListItem(Id wishListItemId);

    public Boolean getDefault();

    public WishList setDefault(Boolean isDefault);

    public WishListAccessType getAccessType();

    public WishList setAccessType(WishListAccessType accessType);

    public String getName();

    public WishList setName(String name);

    static final class Column {
	public static final String ID = "_id";
	public static final String REQUEST_CONTEXT_ID = "req_ctx_id";
	public static final String CUSTOMER_ID = "customer_id";
	public static final String DEFAULT = "default";
	public static final String WISHLIST_ITEMS = "items";
	public static final String ACCESS_TYPE = "ac_type";
	public static final String NAME = "name";
    }
}
