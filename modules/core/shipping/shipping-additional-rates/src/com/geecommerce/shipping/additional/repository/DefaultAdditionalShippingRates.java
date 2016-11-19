package com.geecommerce.shipping.additional.repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.geecommerce.core.service.AbstractRepository;
import com.geecommerce.core.service.annotation.Repository;
import com.geecommerce.shipping.additional.model.AdditionalShippingRate;

@Repository
public class DefaultAdditionalShippingRates extends AbstractRepository implements AdditionalShippingRates {
    @Override
    public List<AdditionalShippingRate> forCarrier(String carrierCode) {
        Map<String, Object> filter = new HashMap<>();
        filter.put(AdditionalShippingRate.Column.CARRIER, carrierCode);

        return simpleContextFind(AdditionalShippingRate.class, filter);
    }
}
