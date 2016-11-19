package com.geecommerce.shipping.fixedrate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.geecommerce.core.App;
import com.geecommerce.shipping.AbstractShippingCalculationMethod;
import com.geecommerce.shipping.annotation.ShippingCalculationMethod;
import com.geecommerce.shipping.fixedrate.configuration.Key;
import com.geecommerce.shipping.model.ShippingOption;
import com.geecommerce.shipping.model.ShippingPackage;
import com.google.inject.Inject;

@ShippingCalculationMethod
public class FixedRateShipmentCalculationMethod extends AbstractShippingCalculationMethod {
    @Inject
    protected App app;

    @Override
    public boolean isEnabled() {
        return app.cpBool_(Key.CARRIER_ENABLED, false);
    }

    @Override
    public String getCode() {
        return "fixedrate";
    }

    @Override
    public List<ShippingOption> getShipmentOptions(Object... data) {

        ShippingPackage shippingData = (ShippingPackage) data[0];
        String countryCode = shippingData.getShippingAddress().getCountry();

        List<ShippingOption> shipmentOptions = new ArrayList<>();

        List<String> names = getRateNames();
        if (names == null)
            return shipmentOptions;

        for (String name : names) {
            if (app.cpBool_(getFullKey(name, Key.ENABLED), false)) {
                String countries = app.cpStr_(getFullKey(name, Key.COUNTRIES));
                if (countries.contains(countryCode)) {
                    ShippingOption option = app.getInjectable(ShippingOption.class);
                    option.setRate(app.cpDouble_(getFullKey(name, Key.PRICE)));
                    option.setName(app.cpStr_(getFullKey(name, Key.NAME)));
                    option.setDescription(app.cpStr_(getFullKey(name, Key.DESCRIPTION)));
                    option.setCarrierCode(getCode());
                    option.setOptionCode(name);
                    shipmentOptions.add(option);
                }
            }
        }
        return shipmentOptions;
    }

    private String getFullKey(String rate, String key) {
        return Key.BASE_RATE + "/" + rate + "/" + key;
    }

    private List<String> getRateNames() {
        String names = app.cpStr_(Key.RATE_NAMES);
        if (names == null && names.isEmpty())
            return null;
        return Arrays.asList(names.split(","));
    }
}
