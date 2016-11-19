package com.geecommerce.coupon.model;

import com.geecommerce.core.service.api.Model;
import com.geecommerce.core.type.Id;

import java.util.Date;

public interface CouponUsage extends Model {

    public Id getCustomerId();

    public CouponUsage setCustomerId(Id customerId);

    public Id getOrderId();

    public CouponUsage setOrderId(Id orderId);

    public Date getUsageDate();

    public CouponUsage setUsageDate(Date usageDate);

    public String getEmail();

    public CouponUsage setEmail(String email);

    static final class Column {
	public static final String CUSTOMER_ID = "customer_id";
	public static final String EMAIL = "email";
	public static final String USAGE_DATE = "u_date";
	public static final String ORDER_ID = "order_id";
    }
}
