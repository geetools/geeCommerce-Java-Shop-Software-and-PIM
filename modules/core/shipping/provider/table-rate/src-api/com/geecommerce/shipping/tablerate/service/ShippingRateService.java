package com.geecommerce.shipping.tablerate.service;

import com.geecommerce.core.service.api.Service;
import com.geecommerce.shipping.enums.ShippingType;
import com.geecommerce.shipping.tablerate.ShippingRateType;
import com.geecommerce.shipping.tablerate.model.ShippingRate;

public interface ShippingRateService extends Service {
    public ShippingRate findShippingRateFor(ShippingType shippingType, ShippingRateType type, String countryCode,
        String stateCode, String zipCode, Double value);
}
