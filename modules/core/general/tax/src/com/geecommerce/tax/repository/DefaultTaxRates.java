package com.geecommerce.tax.repository;

import java.util.HashMap;
import java.util.Map;

import com.geecommerce.core.service.AbstractRepository;
import com.geecommerce.core.service.annotation.Repository;
import com.geecommerce.tax.model.TaxClass;
import com.geecommerce.tax.model.TaxRate;

@Repository
public class DefaultTaxRates extends AbstractRepository implements TaxRates {
    @Override
    public TaxRate forZip(String countryCode, String zipCode, TaxClass taxClass) {
        Map<String, Object> filter = new HashMap<>();
        filter.put(TaxRate.Column.COUNTRY, countryCode);
        filter.put(TaxRate.Column.ZIP, zipCode);
        filter.put(TaxRate.Column.PRODUCT_TAX_CLASS_CODE, taxClass.getCode());

        return multiContextFindOne(TaxRate.class, filter);
    }

    @Override
    public TaxRate forState(String countryCode, String stateCode, TaxClass taxClass) {
        Map<String, Object> filter = new HashMap<>();
        filter.put(TaxRate.Column.COUNTRY, countryCode);
        filter.put(TaxRate.Column.STATE, stateCode);
        filter.put(TaxRate.Column.ZIP, null);
        filter.put(TaxRate.Column.PRODUCT_TAX_CLASS_CODE, taxClass.getCode());

        return multiContextFindOne(TaxRate.class, filter);
    }

    @Override
    public TaxRate forCountry(String countryCode, TaxClass taxClass) {
        Map<String, Object> filter = new HashMap<>();
        filter.put(TaxRate.Column.COUNTRY, countryCode);
        filter.put(TaxRate.Column.STATE, null);
        filter.put(TaxRate.Column.ZIP, null);
        filter.put(TaxRate.Column.PRODUCT_TAX_CLASS_CODE, taxClass.getCode());

        return multiContextFindOne(TaxRate.class, filter);
    }
}
