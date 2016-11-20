package com.geecommerce.tax.service;

import com.geecommerce.core.App;
import com.geecommerce.core.service.annotation.Service;
import com.geecommerce.tax.TaxClassType;
import com.geecommerce.tax.model.TaxClass;
import com.geecommerce.tax.model.TaxRate;
import com.geecommerce.tax.repository.TaxClasses;
import com.geecommerce.tax.repository.TaxRates;
import com.google.inject.Inject;

@Service
public class DefaultTaxService implements TaxService {
    @Inject
    protected App app;

    protected final TaxClasses taxClasses;
    protected final TaxRates taxRates;

    @Inject
    public DefaultTaxService(TaxClasses taxClasses, TaxRates taxRates) {
        this.taxClasses = taxClasses;
        this.taxRates = taxRates;
    }

    @Override
    public TaxRate findDefaultTaxRateFor(TaxClass productTaxClass) {
        String defaultCountry = app.cpStr_("tax/default/tax_country");
        String defaultState = app.cpStr_("tax/default/tax_state");
        String defaultZip = app.cpStr_("tax/default/tax_zip");

        if (defaultCountry == null || "".equals(defaultCountry.trim())) {
            throw new RuntimeException(
                "The default configuration property 'tax/default/tax_country' has not been set.");
        }

        if (defaultState == null || "".equals(defaultState.trim())) {
            defaultState = null;
        }

        if (defaultZip == null || "".equals(defaultZip.trim())) {
            defaultZip = null;
        }

        return findTaxRateFor(productTaxClass, defaultCountry, defaultState, defaultZip);
    }

    @Override
    public TaxRate findTaxRateFor(TaxClass productTaxClass, String countryCode, String stateCode, String zipCode) {
        TaxRate taxRate = null;

        if (zipCode != null) {
            taxRate = taxRates.forZip(countryCode, zipCode, productTaxClass);
        }

        if (taxRate == null && stateCode != null) {
            taxRate = taxRates.forState(countryCode, stateCode, productTaxClass);
        }

        if (taxRate == null && countryCode != null) {
            taxRate = taxRates.forCountry(countryCode, productTaxClass);
        }

        return taxRate;
    }

    @Override
    public TaxClass findTaxClassFor(String code, TaxClassType taxClassType) {
        return taxClasses.havingCode(code, taxClassType);
    }
}
