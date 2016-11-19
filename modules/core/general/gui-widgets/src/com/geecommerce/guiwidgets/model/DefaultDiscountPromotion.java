package com.geecommerce.guiwidgets.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.geecommerce.catalog.product.service.ProductService;
import com.geecommerce.core.service.AbstractModel;
import com.geecommerce.core.service.annotation.Column;
import com.geecommerce.core.service.annotation.Model;
import com.geecommerce.core.type.ContextObject;
import com.geecommerce.core.type.Id;
import com.geecommerce.coupon.model.Coupon;
import com.geecommerce.coupon.service.CouponService;
import com.google.common.collect.Maps;
import com.google.inject.Inject;

@Model(collection = "discount_promotions", optimisticLocking = true)
public class DefaultDiscountPromotion extends AbstractModel implements DiscountPromotion {

    @Column(Col.ID)
    private Id id = null;

    @Column(Col.KEY)
    private String key = null;

    @Column(Col.LABEL)
    private ContextObject<String> label = null;

    @Column(Col.DESCRIPTION)
    private ContextObject<String> description = null;

    @Column(Col.DESCRIPTION_EMAIL)
    private ContextObject<String> descriptionEmail = null;

    @Column(Col.TITLE_PROMO_PAGE)
    private ContextObject<String> titlePromoPage = null;

    @Column(Col.DESCRIPTION_PROMO_PAGE)
    private ContextObject<String> descriptionPromoPage = null;

    @Column(Col.COUPON_ID)
    private Id couponId = null;

    private Coupon coupon = null;

    @Column(Col.SHOW_TIMES)
    private Integer showTimes = null;

    @Column(Col.SHOW_FROM)
    private Date showFrom = null;

    @Column(Col.SHOW_TO)
    private Date showTo = null;

    @Column(Col.RERUN_AFTER)
    private Integer rerunAfter = null;

    @Column(Col.COUPON_DURATION)
    private Integer couponDuration = null;

    @Column(Col.SHOW_FOR_ALL_CUSTOMERS)
    private Boolean showForAll = null;

    private List<ActionGift> gifts = null;

    @Column(Col.EMAIL_TEMPLATE)
    private String emailTemplateCode = null;

    @Column(Col.ENABLED)
    private ContextObject<Boolean> enabled;

    private final CouponService couponService;
    private final ProductService productService;

    @Inject
    public DefaultDiscountPromotion(CouponService couponService, ProductService productService) {
        this.couponService = couponService;
        this.productService = productService;
    }

    @Override
    public Id getId() {
        return id;
    }

    @Override
    public DiscountPromotion setId(Id id) {
        this.id = id;
        return this;
    }

    @Override
    public String getKey() {
        return key;
    }

    @Override
    public DiscountPromotion setKey(String key) {
        this.key = key;
        return this;
    }

    @Override
    public ContextObject<String> getLabel() {
        return label;
    }

    @Override
    public DiscountPromotion setLabel(ContextObject<String> label) {
        this.label = label;
        return this;
    }

    @Override
    public ContextObject<String> getDescription() {
        return description;
    }

    @Override
    public DiscountPromotion setDescription(ContextObject<String> description) {
        this.description = description;
        return this;
    }

    @Override
    public ContextObject<String> getDescriptionEmail() {
        return descriptionEmail;
    }

    @Override
    public DiscountPromotion setDescriptionEmail(ContextObject<String> descriptionEmail) {
        this.descriptionEmail = descriptionEmail;
        return this;
    }

    @Override
    public ContextObject<String> getTitlePromoPage() {
        return titlePromoPage;
    }

    @Override
    public DiscountPromotion setTitlePromoPage(ContextObject<String> titlePromoPage) {
        this.titlePromoPage = titlePromoPage;
        return this;
    }

    @Override
    public ContextObject<String> getDescriptionPromoPage() {
        return descriptionPromoPage;
    }

    @Override
    public DiscountPromotion setDescriptionPromoPage(ContextObject<String> descriptionPromoPage) {
        this.descriptionPromoPage = descriptionPromoPage;
        return this;
    }

    @Override
    public Coupon getCoupon() {
        if (coupon == null && couponId != null)
            coupon = couponService.getCoupon(couponId);
        return coupon;
    }

    @Override
    public DiscountPromotion setCoupon(Coupon coupon) {
        this.coupon = coupon;
        if (coupon != null) {
            this.couponId = coupon.getId();
        } else {
            this.couponId = null;
        }
        return this;
    }

    @Override
    public Id getCouponId() {
        return couponId;
    }

    @Override
    public DiscountPromotion setCouponId(Id couponId) {
        coupon = null;
        this.couponId = couponId;
        return this;
    }

    @Override
    public Integer getShowTimes() {
        return showTimes;
    }

    @Override
    public DiscountPromotion setShowTimes(Integer showTimes) {
        this.showTimes = showTimes;
        return this;
    }

    @Override
    public Integer getRerunAfter() {
        return rerunAfter;
    }

    @Override
    public DiscountPromotion setRerunAfter(Integer rerunAfter) {
        this.rerunAfter = rerunAfter;
        return this;
    }

    @Override
    public Integer getCouponDuration() {
        return couponDuration;
    }

    @Override
    public DiscountPromotion setCouponDuration(Integer couponDuration) {
        this.couponDuration = couponDuration;
        return this;
    }

    @Override
    public Date getShowFrom() {
        return showFrom;
    }

    @Override
    public DiscountPromotion setShowFrom(Date showFrom) {
        this.showFrom = showFrom;
        return this;
    }

    @Override
    public Date getShowTo() {
        return showTo;
    }

    @Override
    public DiscountPromotion setShowTo(Date showTo) {
        this.showTo = showTo;
        return this;
    }

    @Override
    public Boolean getShowForAll() {
        return showForAll;
    }

    @Override
    public DiscountPromotion setShowForAll(Boolean showForAll) {
        this.showForAll = showForAll;
        return this;
    }

    @Override
    public List<ActionGift> getGifts() {
        return gifts;
    }

    @Override
    public DiscountPromotion setGifts(List<ActionGift> gifts) {
        this.gifts = gifts;
        return this;
    }

    @Override
    public String getEmailTemplateCode() {
        return emailTemplateCode;
    }

    @Override
    public DiscountPromotion setEmailTemplateCode(String emailTemplateCode) {
        this.emailTemplateCode = emailTemplateCode;
        return this;
    }

    @Override
    public ContextObject<Boolean> getEnabled() {
        return enabled;
    }

    @Override
    public DiscountPromotion setEnabled(ContextObject<Boolean> enabled) {
        this.enabled = enabled;
        return this;
    }

    @Override
    public void fromMap(Map<String, Object> map) {
        if (map == null)
            return;

        super.fromMap(map);

        gifts = new ArrayList<>();

        List<Map<String, Object>> giftsMap = list_(map.get(Col.GIFTS));

        if (giftsMap != null && giftsMap.size() > 0) {
            for (Map<String, Object> giftMap : giftsMap) {
                ActionGift actionGift = app.getModel(ActionGift.class);
                actionGift.fromMap(giftMap);
                gifts.add(actionGift);
            }
        }

    }

    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> map = Maps.newLinkedHashMap(super.toMap());

        List<Map<String, Object>> giftsList = new ArrayList<>();
        if (getGifts() != null) {
            for (ActionGift gift : getGifts()) {
                giftsList.add(gift.toMap());
            }
            map.put(Col.GIFTS, giftsList);
        }

        return map;
    }

}
