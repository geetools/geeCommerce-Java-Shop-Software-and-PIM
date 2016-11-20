package com.geecommerce.coupon.model;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.geecommerce.core.service.AbstractModel;
import com.geecommerce.core.service.annotation.Model;
import com.geecommerce.core.type.Id;

@Model
public class DefaultCouponUsage extends AbstractModel implements CouponUsage {

    private Id customerId;
    private Id orderId;
    private Date usageDate;
    private String email;

    @Override
    public Id getCustomerId() {
        return customerId;
    }

    @Override
    public CouponUsage setCustomerId(Id customerId) {
        this.customerId = customerId;
        return this;
    }

    @Override
    public Id getOrderId() {
        return orderId;
    }

    @Override
    public CouponUsage setOrderId(Id orderId) {
        this.orderId = orderId;
        return this;
    }

    @Override
    public Date getUsageDate() {
        return usageDate;
    }

    @Override
    public CouponUsage setUsageDate(Date usageDate) {
        this.usageDate = usageDate;
        return this;
    }

    @Override
    public String getEmail() {
        return email;
    }

    @Override
    public CouponUsage setEmail(String email) {
        this.email = email;
        return this;
    }

    @Override
    public void fromMap(Map<String, Object> map) {
        this.customerId = id_(map.get(Column.CUSTOMER_ID));
        this.orderId = id_(map.get(Column.ORDER_ID));
        this.usageDate = date_(map.get(Column.USAGE_DATE));
        this.email = str_(map.get(Column.EMAIL));
    }

    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> m = new HashMap<>();
        m.put(Column.CUSTOMER_ID, getCustomerId());
        m.put(Column.ORDER_ID, getOrderId());
        m.put(Column.USAGE_DATE, getUsageDate());
        m.put(Column.EMAIL, getEmail());
        return m;
    }

    @Override
    public Id getId() {
        return null;
    }

}
