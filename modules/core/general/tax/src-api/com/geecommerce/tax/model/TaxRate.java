package com.geecommerce.tax.model;

import com.geecommerce.core.service.api.MultiContextModel;
import com.geecommerce.core.type.ContextObject;
import com.geecommerce.core.type.Id;

public interface TaxRate extends MultiContextModel {
    public TaxRate setId(Id id);

    public String getCountry();

    public TaxRate setCountry(String country);

    public String getState();

    public TaxRate setState(String state);

    public String getZip();

    public TaxRate setZip(String zip);

    public TaxClass getProductTaxClass();

    public TaxRate setProductTaxClass(TaxClass productTaxClass);

    public Double getRate();

    public TaxRate setRate(Double rate);

    public ContextObject<String> getLabel();

    public TaxRate setLabel(ContextObject<String> label);

    static final class Column {
	public static final String ID = "_id";
	public static final String COUNTRY = "country";
	public static final String STATE = "state";
	public static final String ZIP = "zip";
	public static final String PRODUCT_TAX_CLASS_CODE = "prd_tax_class";
	public static final String RATE = "rate";
	public static final String LABEL = "label";
    }
}
