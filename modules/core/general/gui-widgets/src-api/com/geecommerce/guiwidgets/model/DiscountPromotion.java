package com.geecommerce.guiwidgets.model;

import java.util.Date;
import java.util.List;

import com.geecommerce.core.service.api.Model;
import com.geecommerce.core.type.ContextObject;
import com.geecommerce.core.type.Id;
import com.geecommerce.coupon.model.Coupon;

public interface DiscountPromotion extends Model {

    public DiscountPromotion setId(Id id);

    public String getKey();

    public DiscountPromotion setKey(String key);

    public ContextObject<String> getLabel();

    public DiscountPromotion setLabel(ContextObject<String> label);

    public ContextObject<String> getDescription();

    public DiscountPromotion setDescription(ContextObject<String> description);

    public ContextObject<String> getDescriptionEmail();

    public DiscountPromotion setDescriptionEmail(ContextObject<String> descriptionEmail);

    public ContextObject<String> getTitlePromoPage();

    public DiscountPromotion setTitlePromoPage(ContextObject<String> titlePromoPage);

    public ContextObject<String> getDescriptionPromoPage();

    public DiscountPromotion setDescriptionPromoPage(ContextObject<String> descriptionPromoPage);

    public Coupon getCoupon();

    public DiscountPromotion setCoupon(Coupon coupon);

    public Id getCouponId();

    public DiscountPromotion setCouponId(Id couponId);

    public Integer getShowTimes();

    public DiscountPromotion setShowTimes(Integer showTimes);

    public Integer getRerunAfter(); // hours

    public DiscountPromotion setRerunAfter(Integer rerunAfter); // hours

    public Integer getCouponDuration(); // days

    public DiscountPromotion setCouponDuration(Integer couponDuration); // days

    public Date getShowFrom();

    public DiscountPromotion setShowFrom(Date showFrom);

    public Date getShowTo();

    public DiscountPromotion setShowTo(Date showTo);

    public Boolean getShowForAll();

    public DiscountPromotion setShowForAll(Boolean showForAll);

    public List<ActionGift> getGifts();

    public DiscountPromotion setGifts(List<ActionGift> gifts);

    public String getEmailTemplateCode();

    public DiscountPromotion setEmailTemplateCode(String emailTemplateCode);

    public ContextObject<Boolean> getEnabled();

    public DiscountPromotion setEnabled(ContextObject<Boolean> enabled);

    static final class Col {
        public static final String ID = "_id";
        public static final String KEY = "key";
        public static final String LABEL = "label";
        public static final String DESCRIPTION = "description";
        public static final String DESCRIPTION_EMAIL = "description_email";
        public static final String TITLE_PROMO_PAGE = "title_promo_page";
        public static final String DESCRIPTION_PROMO_PAGE = "description_promo_page";
        public static final String COUPON_ID = "coupon_id";
        public static final String SHOW_TIMES = "show_times";
        public static final String SHOW_FROM = "show_from";
        public static final String SHOW_TO = "show_to";
        public static final String RERUN_AFTER = "rerun_after";
        public static final String COUPON_DURATION = "coupon_duration";
        public static final String SHOW_FOR_ALL_CUSTOMERS = "show_for_all";
        public static final String GIFTS = "gifts";
        public static final String EMAIL_TEMPLATE = "email_template";
        public static final String ENABLED = "enabled";

    }

}
