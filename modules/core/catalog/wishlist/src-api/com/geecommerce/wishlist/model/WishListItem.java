package com.geecommerce.wishlist.model;

import com.geecommerce.catalog.product.model.Product;
import com.geecommerce.core.service.api.Model;
import com.geecommerce.core.type.Id;

public interface WishListItem extends Model {
    public Id getId();

    public WishListItem setId(Id id);

    public Id getProductId();

    public Product getProduct();

    public WishListItem setProduct(Product product);

    public String getProductName();

    public String getProductURI();

    public Integer getProductQuantity();

    public Boolean getAvailable();

    public Double getProductPrice();

    public Double getProductTaxRate();

    public String getIdea();

    public WishListItem setIdea(String idea);

    public WishListItemType getType();

    public WishListItem setType(WishListItemType type);

    static final class Column {
	public static final String ID = "_id";
	public static final String PRODUCT_ID = "prd_id";
	public static final String PRODUCT_NAME = "prd_name";
	public static final String PRODUCT_PRICE = "prd_price";
	public static final String PRODUCT_TAX_RATE = "prd_tax_rate";
	public static final String IDEA = "idea";
	public static final String WISH_ITEM_TYPE = "type";
	public static final String CREATED_ON = "cr_on";
	public static final String MODIFIED_ON = "mod_on";
    }
}
