package com.geecommerce.shipping.tablerate;

import java.util.ArrayList;
import java.util.List;

import com.geecommerce.core.App;
import com.geecommerce.shipping.AbstractShippingCalculationMethod;
import com.geecommerce.shipping.annotation.ShippingCalculationMethod;
import com.geecommerce.shipping.model.ShippingItem;
import com.geecommerce.shipping.model.ShippingOption;
import com.geecommerce.shipping.model.ShippingPackage;
import com.geecommerce.shipping.tablerate.configuration.Key;
import com.geecommerce.shipping.tablerate.model.ShippingRate;
import com.geecommerce.shipping.tablerate.service.ShippingRateService;
import com.google.inject.Inject;

@ShippingCalculationMethod
public class TableRateShipmentCalculationMethod extends AbstractShippingCalculationMethod {
    @Inject
    protected App app;

    protected final ShippingRateService shippingRateService;

    @Inject
    public TableRateShipmentCalculationMethod(ShippingRateService shippingRateService) {
        this.shippingRateService = shippingRateService;
    }

    @Override
    public boolean isEnabled() {
        return app.cpBool_(Key.ENABLED, false);
    }

    @Override
    public String getCode() {
        return "table_rate";
    }

    @Override
    public List<ShippingOption> getShipmentOptions(Object... data) {

        ShippingPackage shippingData = (ShippingPackage) data[0];
        String countryCode = shippingData.getShippingAddress().getCountry();
        // String state = shippingData.getShippingAddress().getState();
        String zip = shippingData.getShippingAddress().getZip();

        List<ShippingOption> shipmentOptions = new ArrayList<>();

        ShippingRateType type = ShippingRateType.fromId(app.cpInt_(Key.RATE_TYPE));
        if (type == null)
            return shipmentOptions;
        Double value = 0.0;

        if (type == ShippingRateType.PRICE) {
            value = shippingData.getTotalAmount();
        } else if (type == ShippingRateType.WEIGHT) {
            for (ShippingItem item : shippingData.getShippingItems()) {
                value += item.getWeight() * item.getQuantity();
            }
        } else if (type == ShippingRateType.NUMBER_OF_ITEMS) {
            for (ShippingItem item : shippingData.getShippingItems()) {
                value += item.getQuantity();
            }
        }

        ShippingRate shippingRate = shippingRateService.findShippingRateFor(shippingData.getType(), type, countryCode, null, zip, value);
        if (shippingRate == null)
            return shipmentOptions;

        ShippingOption option = app.getInjectable(ShippingOption.class);
        option.setRate(shippingRate.getRate());
        option.setCarrierCode(getCode());
        option.setOptionCode(shippingRate.getId().toString());
        option.setName(shippingRate.getLabel().getVal());
        option.setDescription(shippingRate.getDescription().getVal());

        shipmentOptions.add(option);

        return shipmentOptions;
    }

}
