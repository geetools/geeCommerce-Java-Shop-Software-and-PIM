package com.geecommerce.coupon.enums;

import java.util.Map;

import com.geecommerce.core.App;
import com.geecommerce.core.service.api.ModelEnum;
import com.google.common.collect.Maps;

public enum CouponFilterNodeType implements ModelEnum {
    BOOLEAN_OPERATION(1), FIlTER_ATTRIBUTE_OPERATION(2), FOUND(3), NOT_FOUND(4);

    private int id;

    private CouponFilterNodeType(int id) {
        this.id = id;
    }

    public final int toId() {
        return this.id;
    }

    public final String getLabel() {
        return App.get().message(new StringBuilder("enum.").append(CouponFilterNodeType.class.getSimpleName())
            .append(".").append(name()).toString());
    }

    public static final CouponFilterNodeType fromId(int id) {
        for (CouponFilterNodeType couponFilterNodeType : values()) {
            if (couponFilterNodeType.toId() == id) {
                return couponFilterNodeType;
            }
        }

        return null;
    }

    public static final Map<String, Integer> toHumanReadableMap() {
        Map<String, Integer> hrMap = Maps.newTreeMap();

        for (CouponFilterNodeType couponFilterNodeType : values()) {
            hrMap.put(
                App.get()
                    .message(new StringBuilder("enum.").append(CouponFilterNodeType.class.getSimpleName())
                        .append(".").append(couponFilterNodeType.name()).toString()),
                couponFilterNodeType.id);
        }

        return hrMap;
    }

    public static final String toHumanReadableLabel() {
        return App.get().message(new StringBuilder("enum.").append(CouponFilterNodeType.class.getSimpleName())
            .append(".label").toString());
    }
}
