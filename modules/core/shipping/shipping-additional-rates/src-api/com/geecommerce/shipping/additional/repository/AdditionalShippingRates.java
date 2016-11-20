package com.geecommerce.shipping.additional.repository;

import java.util.List;

import com.geecommerce.core.service.api.Repository;
import com.geecommerce.shipping.additional.model.AdditionalShippingRate;

public interface AdditionalShippingRates extends Repository {
    public List<AdditionalShippingRate> forCarrier(String countryCode);
}
