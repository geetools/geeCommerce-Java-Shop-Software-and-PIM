package com.geecommerce.tax.repository;

import java.util.HashMap;
import java.util.Map;

import com.geecommerce.core.service.AbstractRepository;
import com.geecommerce.core.service.annotation.Repository;
import com.geecommerce.tax.TaxClassType;
import com.geecommerce.tax.model.TaxClass;

@Repository
public class DefaultTaxClasses extends AbstractRepository implements TaxClasses {
    public TaxClass havingCode(String code, TaxClassType taxClassType) {
        Map<String, Object> filter = new HashMap<>();
        filter.put(TaxClass.Column.CODE, code);
        filter.put(TaxClass.Column.TAX_CLASS_TYPE, taxClassType.toId());

        return multiContextFindOne(TaxClass.class, filter);
    }
}
