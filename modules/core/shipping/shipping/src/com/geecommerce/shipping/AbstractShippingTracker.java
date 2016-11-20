package com.geecommerce.shipping;

import java.util.List;

import com.geecommerce.shipping.model.ShippingEvent;

public abstract class AbstractShippingTracker {
    public abstract String getCode();

    public abstract String isMatch(String trackingNumber);

    public abstract String getUrl(String trackingNumber);

    public abstract List<ShippingEvent> getShipmentEvents(String trackingNumber);
}
