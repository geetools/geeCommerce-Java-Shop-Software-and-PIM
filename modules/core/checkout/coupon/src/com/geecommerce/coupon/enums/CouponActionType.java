package com.geecommerce.coupon.enums;

import java.util.Map;

import com.geecommerce.core.App;
import com.geecommerce.core.service.api.ModelEnum;
import com.google.common.collect.Maps;

public enum CouponActionType implements ModelEnum {
    PERCENT_PRODUCT(1), FIXED_PRODUCT(2), PERCENT_CART(3), FIXED_CART(4), BUY_X_GET_Y_FREE(5), SPEND_X_GET_Y_FREE(6),
    RANGE_PERCENT_CART(7), RANGE_FIXED_CART(8), LIST_PERCENT_PRODUCT(9), LIST_FIXED_PRODUCT(11), BUY_X_GET_Y_FREE_SAME(12);

    private int id;

    private CouponActionType(int id) {
        this.id = id;
    }

    public final int toId() {
        return this.id;
    }

    public final String getLabel() {
        return App.get().message(new StringBuilder("enum.").append(CouponActionType.class.getSimpleName()).append(".")
            .append(name()).toString());
    }

    public static final CouponActionType fromId(int id) {
        for (CouponActionType couponActionType : values()) {
            if (couponActionType.toId() == id) {
                return couponActionType;
            }
        }

        return null;
    }

    public static final Map<String, Integer> toHumanReadableMap() {
        Map<String, Integer> hrMap = Maps.newTreeMap();

        for (CouponActionType couponActionType : values()) {
            hrMap.put(App.get().message(new StringBuilder("enum.").append(CouponActionType.class.getSimpleName())
                .append(".").append(couponActionType.name()).toString()), couponActionType.id);
        }

        return hrMap;
    }

    public static final String toHumanReadableLabel() {
        return App.get().message(
            new StringBuilder("enum.").append(CouponActionType.class.getSimpleName()).append(".label").toString());
    }
}
