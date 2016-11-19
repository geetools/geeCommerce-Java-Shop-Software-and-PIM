package com.geecommerce.shipping;

import com.geecommerce.shipping.model.ShippingEvent;
import java.util.List;

public abstract class AbstractShippingTracker {
    public abstract String getCode();

    public abstract String isMatch(String trackingNumber);

    public abstract String getUrl(String trackingNumber);

    public abstract List<ShippingEvent> getShipmentEvents(String trackingNumber);
}
