package com.geecommerce.shipping.tablerate.service;

import java.util.List;

import com.geecommerce.core.service.annotation.Service;
import com.geecommerce.shipping.enums.ShippingType;
import com.geecommerce.shipping.tablerate.ShippingRateType;
import com.geecommerce.shipping.tablerate.model.ShippingRate;
import com.geecommerce.shipping.tablerate.repository.ShippingRates;
import com.google.inject.Inject;

@Service
public class DefaultShippingRateService implements ShippingRateService {
    private final ShippingRates shippingRates;

    @Inject
    public DefaultShippingRateService(ShippingRates shippingRates) {
	this.shippingRates = shippingRates;
    }

    private ShippingRate findShippingRate(List<ShippingRate> rates, Double value) {
	ShippingRate rate = null;
	for (ShippingRate rt : rates) {
	    if (rt.getLowerBound() <= value) {
		if (rate == null || rate.getLowerBound() < rt.getLowerBound()) {
		    rate = rt;
		}
	    }
	}
	return rate;
    }

    
    @Override
    public ShippingRate findShippingRateFor(ShippingType shippingType, ShippingRateType type, String countryCode, String stateCode, String zipCode, Double value) {
	ShippingRate shippingRate = null;
	List<ShippingRate> shippingRateList = null;

	if (zipCode != null) {
	    shippingRateList = shippingRates.forZip(shippingType, type, countryCode, zipCode);
	}

	if ((shippingRateList == null || shippingRateList.size() == 0) && stateCode != null) {
	    shippingRateList = shippingRates.forState(shippingType, type, countryCode, stateCode);
	}

	if ((shippingRateList == null || shippingRateList.size() == 0) && countryCode != null) {
	    shippingRateList = shippingRates.forCountry(shippingType, type, countryCode);
	}

	if (shippingRateList == null || shippingRateList.size() == 0)
	    return null;

	return findShippingRate(shippingRateList, value);
    }

}
