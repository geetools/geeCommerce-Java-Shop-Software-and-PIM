package com.geecommerce.coupon.proxy;

import com.geecommerce.catalog.product.model.Product;
import com.geecommerce.core.service.api.Model;
import com.geecommerce.core.type.Id;
import com.geecommerce.price.model.Price;
import com.geecommerce.price.model.PriceType;

public interface CouponCartItemProxy extends Model {
    public Id getProductId();

    public Product getProduct();

    public CouponCartItemProxy setProduct(Product product);

    public String getProductName();

    public String getProductURI();

    public Price productPrice();

    public Double getProductPrice();

    public PriceType getProductPriceType();

    public Double getProductTaxRate();

    public int getQuantity();

    public CouponCartItemProxy setQuantity(int quantity);

    public CouponCartItemProxy incrementQty();

    public Double getPackageWeight();

    public Double getPackageWidth();

    public Double getPackageHeight();

    public Double getPackageDepth();

    public Boolean isLast();

    public CouponCartItemProxy setLast(Boolean last);
}
