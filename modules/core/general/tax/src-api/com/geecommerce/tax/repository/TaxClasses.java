package com.geecommerce.tax.repository;

import com.geecommerce.core.service.api.Repository;
import com.geecommerce.tax.TaxClassType;
import com.geecommerce.tax.model.TaxClass;

public interface TaxClasses extends Repository {
    public TaxClass havingCode(String code, TaxClassType taxClassType);
}
