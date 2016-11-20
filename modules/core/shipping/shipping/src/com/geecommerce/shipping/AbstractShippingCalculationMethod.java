package com.geecommerce.shipping;

import java.util.List;

import com.geecommerce.shipping.model.ShippingOption;

public abstract class AbstractShippingCalculationMethod {
    public abstract boolean isEnabled();

    public abstract String getCode();

    public abstract List<ShippingOption> getShipmentOptions(Object... data);
}
