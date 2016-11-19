package com.geecommerce.shipping;

import com.geecommerce.shipping.model.ShippingOption;
import java.util.List;

public abstract class AbstractShippingCalculationMethod {
    public abstract boolean isEnabled();

    public abstract String getCode();

    public abstract List<ShippingOption> getShipmentOptions(Object... data);
}
