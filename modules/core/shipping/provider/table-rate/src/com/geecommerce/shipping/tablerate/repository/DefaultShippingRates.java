package com.geecommerce.shipping.tablerate.repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.geecommerce.core.service.AbstractRepository;
import com.geecommerce.core.service.annotation.Repository;
import com.geecommerce.shipping.enums.ShippingType;
import com.geecommerce.shipping.tablerate.ShippingRateType;
import com.geecommerce.shipping.tablerate.model.ShippingRate;

@Repository
public class DefaultShippingRates extends AbstractRepository implements ShippingRates {
    @Override
    public List<ShippingRate> forZip(ShippingType shippingType, ShippingRateType type, String countryCode,
        String zipCode) {
        Map<String, Object> filter = new HashMap<>();
        filter.put(ShippingRate.Column.RATE_TYPE, type.toId());
        if (shippingType != null) {
            filter.put(ShippingRate.Column.SHIPPING_TYPE, shippingType.toId());
        }
        filter.put(ShippingRate.Column.COUNTRY, countryCode);
        filter.put(ShippingRate.Column.ZIP, zipCode);

        return simpleContextFind(ShippingRate.class, filter);
    }

    @Override
    public List<ShippingRate> forState(ShippingType shippingType, ShippingRateType type, String countryCode,
        String stateCode) {
        Map<String, Object> filter = new HashMap<>();
        filter.put(ShippingRate.Column.RATE_TYPE, type.toId());
        if (shippingType != null) {
            filter.put(ShippingRate.Column.SHIPPING_TYPE, shippingType.toId());
        }
        filter.put(ShippingRate.Column.COUNTRY, countryCode);
        filter.put(ShippingRate.Column.STATE, stateCode);
        filter.put(ShippingRate.Column.ZIP, null);

        return simpleContextFind(ShippingRate.class, filter);
    }

    @Override
    public List<ShippingRate> forCountry(ShippingType shippingType, ShippingRateType type, String countryCode) {
        Map<String, Object> filter = new HashMap<>();
        filter.put(ShippingRate.Column.RATE_TYPE, type.toId());
        if (shippingType != null) {
            filter.put(ShippingRate.Column.SHIPPING_TYPE, shippingType.toId());
        }
        filter.put(ShippingRate.Column.COUNTRY, countryCode);
        filter.put(ShippingRate.Column.STATE, null);
        filter.put(ShippingRate.Column.ZIP, null);

        return simpleContextFind(ShippingRate.class, filter);
    }
}
