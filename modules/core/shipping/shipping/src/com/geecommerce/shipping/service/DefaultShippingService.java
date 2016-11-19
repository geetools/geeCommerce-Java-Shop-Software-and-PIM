package com.geecommerce.shipping.service;

import java.util.ArrayList;
import java.util.List;

import com.geecommerce.core.App;
import com.geecommerce.core.ApplicationContext;
import com.geecommerce.core.inject.ModuleInjector;
import com.geecommerce.core.service.annotation.Service;
import com.geecommerce.shipping.AbstractShippingCalculationMethod;
import com.geecommerce.shipping.annotation.ShippingCalculationMethod;
import com.geecommerce.shipping.model.ShippingAddress;
import com.geecommerce.shipping.model.ShippingOption;
import com.geecommerce.shipping.model.ShippingPackage;
import com.geemodule.Geemodule;
import com.geemodule.api.ModuleLoader;
import com.google.inject.Inject;
import com.google.inject.Injector;

@Service
public class DefaultShippingService implements ShippingService {
    @Inject
    protected App app;

    @SuppressWarnings("unchecked")
    protected List<AbstractShippingCalculationMethod> locateShippingCalculationMethods() {
        List<AbstractShippingCalculationMethod> shippingCalculationMethods = new ArrayList<>();
        ApplicationContext appCtx = app.getApplicationContext();

        if (appCtx != null) {
            ModuleLoader loader = Geemodule.createModuleLoader(appCtx.getMerchant().getModulesPath());

            Class<AbstractShippingCalculationMethod>[] types = (Class<AbstractShippingCalculationMethod>[]) loader.findAllTypesAnnotatedWith(ShippingCalculationMethod.class, false);

            for (Class<AbstractShippingCalculationMethod> type : types) {

                Injector injector = ModuleInjector.get();

                AbstractShippingCalculationMethod shippingCalculationMethod = injector.getInstance(type);// type.newInstance();
                if (shippingCalculationMethod.isEnabled())
                    shippingCalculationMethods.add(shippingCalculationMethod);
            }
        }

        return shippingCalculationMethods;
    }

    protected ShippingOption optionWithLesserPrice(List<ShippingOption> options) {
        if (options == null)
            return null;

        ShippingOption minPriceShippingOption = null;
        Double price = Double.MAX_VALUE;
        for (ShippingOption option : options) {
            if (option.getRate() < price) {
                minPriceShippingOption = option;
                price = option.getRate();
            }
        }
        return minPriceShippingOption;
    }

    @Override
    public List<ShippingOption> getShippingOptions(ShippingPackage shippingPackage) {
        List<AbstractShippingCalculationMethod> shippingCalculationMethods = locateShippingCalculationMethods();
        List<ShippingOption> shippingOptions = new ArrayList<>();
        for (AbstractShippingCalculationMethod shippingCalculationMethod : shippingCalculationMethods) {
            List<ShippingOption> options = shippingCalculationMethod.getShipmentOptions(shippingPackage);
            if (options != null)
                shippingOptions.addAll(options);
        }
        return shippingOptions;
    }

    @Override
    public List<ShippingOption> getShippingOptions(ShippingPackage shippingPackage, String carrier) {
        AbstractShippingCalculationMethod defaultShippingCalculationMethod = null;
        List<AbstractShippingCalculationMethod> shippingCalculationMethods = locateShippingCalculationMethods();

        for (AbstractShippingCalculationMethod shippingCalculationMethod : shippingCalculationMethods) {
            if (shippingCalculationMethod.getCode().equals(carrier)) {
                defaultShippingCalculationMethod = shippingCalculationMethod;
                break;
            }
        }

        if (defaultShippingCalculationMethod == null)
            return null;

        List<ShippingOption> options = defaultShippingCalculationMethod.getShipmentOptions(shippingPackage);
        return options;
    }

    @Override
    public List<ShippingOption> getShippingOptionsExcept(ShippingPackage shippingData, String carrier) {
        List<AbstractShippingCalculationMethod> shippingCalculationMethods = locateShippingCalculationMethods();
        List<ShippingOption> shippingOptions = new ArrayList<>();
        for (AbstractShippingCalculationMethod shippingCalculationMethod : shippingCalculationMethods) {
            if (!shippingCalculationMethod.getCode().equals(carrier)) {
                List<ShippingOption> options = shippingCalculationMethod.getShipmentOptions(shippingData);
                if (options != null)
                    shippingOptions.addAll(options);
            }

        }
        return shippingOptions;
    }

    @Override
    public ShippingOption getEstimatedShippingOption(ShippingPackage shippingData) {
        final String defaultCarrier = app.cpStr_("shipping/default/carrier");

        List<ShippingOption> options = getShippingOptions(shippingData, defaultCarrier);

        return optionWithLesserPrice(options);
    }

    @Override
    public ShippingOption getEstimatedShippingOptionForDefaultAddress(ShippingPackage shippingData) {

        final String defaultCountry = app.cpStr_("shipping/default/destination_country");
        final String defaultState = app.cpStr_("shipping/default/destination_state");
        final String defaultZip = app.cpStr_("shipping/default/destination_zip");
        ShippingAddress shippingAddress = app.getModel(ShippingAddress.class);
        shippingAddress.setCountry(defaultCountry);
        shippingAddress.setState(defaultState);
        shippingAddress.setZip(defaultZip);
        shippingData.setShippingAddress(shippingAddress);
        return getEstimatedShippingOption(shippingData);
    }

    @Override
    public ShippingOption getShippingOption(ShippingPackage shippingData, String carrierCode, String optionCode) {
        List<ShippingOption> options = getShippingOptions(shippingData, carrierCode);

        if (options == null || options.size() == 0)
            return null;

        ShippingOption option = null;
        for (ShippingOption opt : options) {
            if (opt.getOptionCode().equals(optionCode)) {
                option = opt;
                break;
            }
        }
        return option;
    }

    @Override
    public AbstractShippingCalculationMethod getShippingMethod(String carrier) {
        AbstractShippingCalculationMethod defaultShippingCalculationMethod = null;
        List<AbstractShippingCalculationMethod> shippingCalculationMethods = locateShippingCalculationMethods();

        for (AbstractShippingCalculationMethod shippingCalculationMethod : shippingCalculationMethods) {
            if (shippingCalculationMethod.getCode().equals(carrier)) {
                return shippingCalculationMethod;
            }
        }
        return null;
    }
}
