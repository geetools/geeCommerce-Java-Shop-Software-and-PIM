package com.geecommerce.checkout.enums;

import java.util.Map;

import com.geecommerce.core.App;
import com.google.common.collect.Maps;

public enum OrderStatus {
    NEW(10), PENDING(20), ACCEPTED(30), CONFIRMED(40), DISPATCHED(50), DONE(60), READY(70), CANCELED(
        80), PARTIALLY_CANCELED(90), REJECTED(100);

    private int id;

    private OrderStatus(int id) {
        this.id = id;
    }

    public final int toId() {
        return this.id;
    }

    public final String getLabel() {
        return App.get().message(new StringBuilder("enum.").append(OrderStatus.class.getSimpleName()).append(".")
            .append(name()).toString());
    }

    public static final OrderStatus fromId(int id) {
        for (OrderStatus orderStatus : values()) {
            if (orderStatus.toId() == id) {
                return orderStatus;
            }
        }

        return null;
    }

    public static final Map<String, Integer> toHumanReadableMap() {
        Map<String, Integer> hrMap = Maps.newTreeMap();

        for (OrderStatus orderStatus : values()) {
            hrMap.put(App.get().message(new StringBuilder("enum.").append(OrderStatus.class.getSimpleName()).append(".")
                .append(orderStatus.name()).toString()), orderStatus.id);
        }

        return hrMap;
    }

    public static final String toHumanReadableLabel() {
        return App.get().message(
            new StringBuilder("enum.").append(OrderStatus.class.getSimpleName()).append(".label").toString());
    }
}
