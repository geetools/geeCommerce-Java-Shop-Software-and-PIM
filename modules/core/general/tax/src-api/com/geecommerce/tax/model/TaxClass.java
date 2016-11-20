package com.geecommerce.tax.model;

import com.geecommerce.core.service.api.MultiContextModel;
import com.geecommerce.core.type.ContextObject;
import com.geecommerce.core.type.Id;
import com.geecommerce.tax.TaxClassType;

public interface TaxClass extends MultiContextModel {
    public TaxClass setId(Id id);

    public String getCode();

    public TaxClass setCode(String code);

    public TaxClassType getTaxClassType();

    public TaxClass setTaxClassType(TaxClassType taxClassType);

    public ContextObject<String> getLabel();

    public TaxClass setLabel(ContextObject<String> label);

    static final class Column {
        public static final String ID = "_id";
        public static final String CODE = "code";
        public static final String TAX_CLASS_TYPE = "type";
        public static final String LABEL = "label";
    }
}
