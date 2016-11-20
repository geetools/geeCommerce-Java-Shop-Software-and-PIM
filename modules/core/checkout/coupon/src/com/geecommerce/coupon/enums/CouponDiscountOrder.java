package com.geecommerce.coupon.enums;

import java.util.Map;

import com.geecommerce.core.App;
import com.geecommerce.core.service.api.ModelEnum;
import com.google.common.collect.Maps;

public enum CouponDiscountOrder implements ModelEnum {
    ASC(1), DSC(2);

    private int id;

    private CouponDiscountOrder(int id) {
        this.id = id;
    }

    public final int toId() {
        return this.id;
    }

    public final String getLabel() {
        return App.get().message(new StringBuilder("enum.").append(CouponDiscountOrder.class.getSimpleName())
            .append(".").append(name()).toString());
    }

    public static final CouponDiscountOrder fromId(int id) {
        for (CouponDiscountOrder couponDiscountOrder : values()) {
            if (couponDiscountOrder.toId() == id) {
                return couponDiscountOrder;
            }
        }

        return null;
    }

    public static final Map<String, Integer> toHumanReadableMap() {
        Map<String, Integer> hrMap = Maps.newTreeMap();

        for (CouponDiscountOrder couponDiscountOrder : values()) {
            hrMap.put(
                App.get()
                    .message(new StringBuilder("enum.").append(CouponDiscountOrder.class.getSimpleName())
                        .append(".").append(couponDiscountOrder.name()).toString()),
                couponDiscountOrder.id);
        }

        return hrMap;
    }

    public static final String toHumanReadableLabel() {
        return App.get().message(new StringBuilder("enum.").append(CouponDiscountOrder.class.getSimpleName())
            .append(".label").toString());
    }
}
