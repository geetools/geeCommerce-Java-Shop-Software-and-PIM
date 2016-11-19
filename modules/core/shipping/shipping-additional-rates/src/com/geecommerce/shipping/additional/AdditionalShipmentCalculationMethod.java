package com.geecommerce.shipping.additional;

import java.util.ArrayList;
import java.util.List;

import com.geecommerce.core.App;
import com.geecommerce.shipping.AbstractShippingCalculationMethod;
import com.geecommerce.shipping.additional.configuration.Key;
import com.geecommerce.shipping.additional.model.AdditionalShippingRate;
import com.geecommerce.shipping.additional.repository.AdditionalShippingRates;
import com.geecommerce.shipping.annotation.ShippingCalculationMethod;
import com.geecommerce.shipping.model.ShippingOption;
import com.geecommerce.shipping.model.ShippingPackage;
import com.google.inject.Inject;

@ShippingCalculationMethod
public class AdditionalShipmentCalculationMethod extends AbstractShippingCalculationMethod {
    @Inject
    protected App app;

    protected final AdditionalShippingRates additionalShippingRates;// = app.getService(ShippingRateService.class);

    @Inject
    public AdditionalShipmentCalculationMethod(AdditionalShippingRates additionalShippingRates) {
	this.additionalShippingRates = additionalShippingRates;
    }

    @Override
    public boolean isEnabled() {
	return app.cpBool_(Key.ENABLED, false);
    }

    @Override
    public String getCode() {
	return "additional_rate";
    }

    @Override
    public List<ShippingOption> getShipmentOptions(Object... data) {
	if (data.length < 2)
	    return null;

	ShippingPackage shippingData = (ShippingPackage) data[0];
	String carrierCode = (String) data[1];

	List<ShippingOption> shipmentOptions = new ArrayList<>();

	List<AdditionalShippingRate> rates = additionalShippingRates.forCarrier(carrierCode);
	if (rates != null) {
	    for (AdditionalShippingRate shippingRate : rates) {
		ShippingOption option = app.getInjectable(ShippingOption.class);
		option.setRate(shippingRate.getRate());
		option.setCarrierCode(getCode());
		option.setOptionCode(shippingRate.getId().toString());
		option.setName(shippingRate.getLabel().getVal());
		option.setDescription(shippingRate.getDescription().getVal());
		option.setGroup(shippingRate.getGroup());

		shipmentOptions.add(option);

	    }
	}

	return shipmentOptions;
    }

}
