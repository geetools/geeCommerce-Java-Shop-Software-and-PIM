package com.geecommerce.coupon.proxy;

import java.util.Date;
import java.util.List;

import com.geecommerce.catalog.product.model.Product;
import com.geecommerce.core.service.api.Model;
import com.geecommerce.core.system.model.RequestContext;
import com.geecommerce.core.type.Id;
import com.geecommerce.coupon.model.CouponCode;
import com.geecommerce.customer.model.Customer;

public interface CouponCartProxy extends Model {
    public Id getId();

    public CouponCartProxy setId(Id id);

    public Id getRequestContextId();

    public CouponCartProxy fromRequestContext(RequestContext requestContext);

    public Id getCustomerId();

    public CouponCartProxy belongsTo(Customer customer);

    public List<?> getCartItems();

    public Date getCreatedOn();

    public Date getModifiedOn();

    public CouponCartProxy addProduct(Product product);

    public int getTotalQuantity();

    public Boolean getEnabled();

    public CouponCartProxy setEnabled(Boolean enabled);

    public CouponCartProxy setCouponCode(CouponCode couponCode);

    public CouponCode getCouponCode();

    public Object getLast();
}
