package com.geecommerce.shipping.service;

import com.geecommerce.core.service.api.Service;
import com.geecommerce.shipping.AbstractShippingCalculationMethod;
import com.geecommerce.shipping.model.ShippingPackage;
import com.geecommerce.shipping.model.ShippingOption;

import java.util.List;

public interface ShippingService extends Service {
    public List<ShippingOption> getShippingOptions(ShippingPackage shippingPackage, String carrierCode);

    public List<ShippingOption> getShippingOptionsExcept(ShippingPackage shippingPackage, String carrierCode);

    public List<ShippingOption> getShippingOptions(ShippingPackage shippingPackage);

    public ShippingOption getEstimatedShippingOption(ShippingPackage shippingPackage);

    public ShippingOption getEstimatedShippingOptionForDefaultAddress(ShippingPackage shippingPackage);

    public ShippingOption getShippingOption(ShippingPackage shippingPackage, String carrierCode, String optionCode);

    public AbstractShippingCalculationMethod getShippingMethod(String carrierCode);
}
