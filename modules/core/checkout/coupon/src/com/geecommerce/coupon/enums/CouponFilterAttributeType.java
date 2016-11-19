package com.geecommerce.coupon.enums;

import java.util.Map;

import com.geecommerce.core.App;
import com.geecommerce.core.service.api.ModelEnum;
import com.google.common.collect.Maps;

public enum CouponFilterAttributeType implements ModelEnum {
    PRODUCT(1), CART(2), CART_ITEM(3), ORDER(4), ORDER_ITEM(5);

    private int id;

    private CouponFilterAttributeType(int id) {
        this.id = id;
    }

    public final int toId() {
        return this.id;
    }

    public final String getLabel() {
        return App.get().message(new StringBuilder("enum.").append(CouponFilterAttributeType.class.getSimpleName()).append(".").append(name()).toString());
    }

    public static final CouponFilterAttributeType fromId(int id) {
        for (CouponFilterAttributeType couponAttributeType : values()) {
            if (couponAttributeType.toId() == id) {
                return couponAttributeType;
            }
        }

        return null;
    }

    public static final Map<String, Integer> toHumanReadableMap() {
        Map<String, Integer> hrMap = Maps.newTreeMap();

        for (CouponFilterAttributeType couponAttributeType : values()) {
            hrMap.put(App.get().message(new StringBuilder("enum.").append(CouponFilterAttributeType.class.getSimpleName()).append(".").append(couponAttributeType.name()).toString()),
                couponAttributeType.id);
        }

        return hrMap;
    }

    public static final String toHumanReadableLabel() {
        return App.get().message(new StringBuilder("enum.").append(CouponFilterAttributeType.class.getSimpleName()).append(".label").toString());
    }
}
