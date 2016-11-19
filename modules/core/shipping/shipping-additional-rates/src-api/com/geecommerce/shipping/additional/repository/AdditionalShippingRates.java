package com.geecommerce.shipping.additional.repository;

import com.geecommerce.core.service.api.Repository;
import com.geecommerce.shipping.additional.model.AdditionalShippingRate;

import java.util.List;

public interface AdditionalShippingRates extends Repository {
    public List<AdditionalShippingRate> forCarrier(String countryCode);
}
