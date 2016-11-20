package com.geecommerce.guiwidgets.model;

import java.util.Map;

import com.geecommerce.core.service.api.Model;
import com.geecommerce.core.type.Id;

public interface DiscountPromotionSubscription extends Model {

    public DiscountPromotionSubscription setId(Id id);

    public Id getDiscountPromotionId();

    public DiscountPromotionSubscription setDiscountPromotionId(Id discountPromotionId);

    public String getEmail();

    public DiscountPromotionSubscription setEmail(String email);

    public String getCouponCode();

    public DiscountPromotionSubscription setCouponCode(String couponCode);

    public Map<String, Object> getForm();

    public DiscountPromotionSubscription setForm(Map<String, Object> form);

    public Id getGiftId();

    public DiscountPromotionSubscription setGiftId(Id giftId);

    static final class Col {
        public static final String ID = "_id";
        public static final String DISCOUNT_PROMOTION_ID = "dsc_promo_id";
        public static final String EMAIL = "email";
        public static final String COUPON_CODE = "coupon_code";
        public static final String FORM = "form";
        public static final String GIFT_ID = "gift_id";

    }

}
