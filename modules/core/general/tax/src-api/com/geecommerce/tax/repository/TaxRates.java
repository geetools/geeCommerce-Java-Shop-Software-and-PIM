package com.geecommerce.tax.repository;

import com.geecommerce.core.service.api.Repository;
import com.geecommerce.tax.model.TaxClass;
import com.geecommerce.tax.model.TaxRate;

public interface TaxRates extends Repository {
    public TaxRate forZip(String countryCode, String zipCode, TaxClass taxClass);

    public TaxRate forState(String countryCode, String stateCode, TaxClass taxClass);

    public TaxRate forCountry(String countryCode, TaxClass taxClass);
}
