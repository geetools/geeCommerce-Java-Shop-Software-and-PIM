package com.geecommerce.customer.model;

import java.util.Date;

import com.geecommerce.core.service.api.Model;
import com.geecommerce.core.type.Id;
import com.geecommerce.core.type.ProductIdSupport;

public interface ViewedProduct extends Model, ProductIdSupport {
    public Id getId();

    public ViewedProduct setId(Id id);

    public Id getCustomerId();

    public ViewedProduct viewedBy(Customer customer);

    public Id getProductId();

    public ViewedProduct viewedProduct(Id productId);

    public Date getViewedOn();

    public ViewedProduct viewedOn(Date viewedOn);

    static final class Column {
	public static final String ID = "_id";
	public static final String CUSTOMER_ID = "cust_id";
	public static final String PRODUCT_ID = "prd_id";
	public static final String VIEWED_ON = "vwd_on";
    }
}
