package com.geecommerce.shipping.tablerate.repository;

import java.util.List;

import com.geecommerce.core.service.api.Repository;
import com.geecommerce.shipping.enums.ShippingType;
import com.geecommerce.shipping.tablerate.ShippingRateType;
import com.geecommerce.shipping.tablerate.model.ShippingRate;

public interface ShippingRates extends Repository {
    public List<ShippingRate> forZip(ShippingType shippingType, ShippingRateType type, String countryCode,
        String zipCode);

    public List<ShippingRate> forState(ShippingType shippingType, ShippingRateType type, String countryCode,
        String stateCode);

    public List<ShippingRate> forCountry(ShippingType shippingType, ShippingRateType type, String countryCode);
}
