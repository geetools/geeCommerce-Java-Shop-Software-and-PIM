package com.geecommerce.coupon.model;

import java.util.Date;
import java.util.List;

import com.geecommerce.core.service.api.Model;
import com.geecommerce.core.type.Id;
import com.owlike.genson.annotation.JsonIgnore;

public interface CouponCode extends Model {

    public CouponCode setId(Id id);

    public CouponCode belongsTo(Coupon coupon);

    @JsonIgnore
    public Coupon getCoupon();

    public Id getCouponId();

    public String getCode();

    public CouponCode setCode(String code);

    public List<CouponUsage> getCouponUsages();

    public CouponCode useCoupon(Id orderId, Id customerId);

    public CouponCode useCoupon(Id orderId, String email);

    public Date getFromDate();

    public CouponCode setFromDate(Date fromDate);

    public Date getToDate();

    public CouponCode setToDate(Date toDate);

    public String getEmail();

    public CouponCode setEmail(String email);

    public Date getExportedDate();

    public CouponCode setExportedDate(Date exportedDate);

    static final class Column {
        public static final String ID = "_id";
        public static final String CODE = "code";
        public static final String COUPON_ID = "coupon_id";
        public static final String COUPON_USAGE = "coupon_usage";
        public static final String FROM_DATE = "from_d";
        public static final String TO_DATE = "to_d";
        public static final String EMAIL = "email";
        public static final String EXPORTED = "exported";
    }
}
