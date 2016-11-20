package com.geecommerce.core.payment;

import java.util.Map;

import com.geecommerce.core.App;
import com.google.common.collect.Maps;

public enum PaymentStatus {
    PENDING(1), AUTHORIZED(2), PAID(3), PARTIALLYREFUNDED(4), REFUNDED(5), VOIDED(6), CAPTURED(7), RETRY(8), ERROR(100);

    private int id;

    private PaymentStatus(int id) {
        this.id = id;
    }

    public final int toId() {
        return this.id;
    }

    public final String getLabel() {
        return App.get().message(new StringBuilder("enum.").append(PaymentStatus.class.getSimpleName()).append(".")
            .append(name()).toString());
    }

    public static final PaymentStatus fromId(int id) {
        for (PaymentStatus paymentStatus : values()) {
            if (paymentStatus.toId() == id) {
                return paymentStatus;
            }
        }

        return null;
    }

    public static final Map<String, Integer> toHumanReadableMap() {
        Map<String, Integer> hrMap = Maps.newTreeMap();

        for (PaymentStatus paymentStatus : values()) {
            hrMap.put(App.get().message(new StringBuilder("enum.").append(PaymentStatus.class.getSimpleName())
                .append(".").append(paymentStatus.name()).toString()), paymentStatus.id);
        }

        return hrMap;
    }

    public static final String toHumanReadableLabel() {
        return App.get().message(
            new StringBuilder("enum.").append(PaymentStatus.class.getSimpleName()).append(".label").toString());
    }
}
