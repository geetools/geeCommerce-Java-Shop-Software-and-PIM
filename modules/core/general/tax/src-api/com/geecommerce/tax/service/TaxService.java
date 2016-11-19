package com.geecommerce.tax.service;

import com.geecommerce.core.service.api.Service;
import com.geecommerce.tax.TaxClassType;
import com.geecommerce.tax.model.TaxClass;
import com.geecommerce.tax.model.TaxRate;

public interface TaxService extends Service {
    public TaxClass findTaxClassFor(String code, TaxClassType taxClassType);

    public TaxRate findDefaultTaxRateFor(TaxClass productTaxClass);

    public TaxRate findTaxRateFor(TaxClass productTaxClass, String countryCode, String stateCode, String zipCode);
}
