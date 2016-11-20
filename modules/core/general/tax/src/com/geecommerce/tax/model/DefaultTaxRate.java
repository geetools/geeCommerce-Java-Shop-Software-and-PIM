package com.geecommerce.tax.model;

import java.util.Map;

import com.geecommerce.core.service.AbstractMultiContextModel;
import com.geecommerce.core.service.annotation.Cacheable;
import com.geecommerce.core.service.annotation.Model;
import com.geecommerce.core.type.ContextObject;
import com.geecommerce.core.type.Id;
import com.geecommerce.tax.TaxClassType;
import com.geecommerce.tax.repository.TaxClasses;
import com.google.common.collect.Maps;
import com.google.inject.Inject;

@Cacheable
@Model("tax_rates")
public class DefaultTaxRate extends AbstractMultiContextModel implements TaxRate {
    private static final long serialVersionUID = -4897010166894675659L;

    private Id id = null;

    private String country = null;

    private String state = null;

    private String zip = null;

    private String productTaxClassCode = null;

    private Double rate = null;

    private ContextObject<String> label = null;

    // Loaded lazily
    private TaxClass productTaxClass = null;

    // Repository
    private TaxClasses taxClasses = null;

    @Inject
    public DefaultTaxRate(TaxClasses taxClasses) {
        this.taxClasses = taxClasses;
    }

    @Override
    public Id getId() {
        return id;
    }

    @Override
    public TaxRate setId(Id id) {
        this.id = id;
        return this;
    }

    @Override
    public String getCountry() {
        return country;
    }

    @Override
    public TaxRate setCountry(String country) {
        this.country = country;
        return this;
    }

    @Override
    public String getState() {
        return state;
    }

    @Override
    public TaxRate setState(String state) {
        this.state = state;
        return this;
    }

    @Override
    public String getZip() {
        return zip;
    }

    @Override
    public TaxRate setZip(String zip) {
        this.zip = zip;
        return this;
    }

    @Override
    public TaxClass getProductTaxClass() {
        if (productTaxClass == null) {
            productTaxClass = taxClasses.havingCode(productTaxClassCode, TaxClassType.PRODUCT);
        }

        return productTaxClass;
    }

    @Override
    public TaxRate setProductTaxClass(TaxClass productTaxClass) {
        this.productTaxClass = productTaxClass;
        this.productTaxClassCode = productTaxClass.getCode();
        return this;
    }

    @Override
    public Double getRate() {
        return rate;
    }

    @Override
    public TaxRate setRate(Double rate) {
        this.rate = rate;
        return this;
    }

    @Override
    public ContextObject<String> getLabel() {
        return label;
    }

    @Override
    public TaxRate setLabel(ContextObject<String> label) {
        this.label = label;
        return this;
    }

    @Override
    public void fromMap(Map<String, Object> map) {
        super.fromMap(map);

        this.id = id_(map.get(Column.ID));
        this.country = str_(map.get(Column.COUNTRY));
        this.state = str_(map.get(Column.STATE));
        this.zip = str_(map.get(Column.ZIP));
        this.productTaxClassCode = str_(map.get(Column.PRODUCT_TAX_CLASS_CODE));
        this.rate = double_(map.get(Column.RATE));
        this.label = ctxObj_(map.get(Column.LABEL));
    }

    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> map = Maps.newLinkedHashMap(super.toMap());

        map.put(Column.ID, getId());

        if (getCountry() != null)
            map.put(Column.COUNTRY, getCountry());

        if (getState() != null)
            map.put(Column.STATE, getState());

        if (getZip() != null)
            map.put(Column.ZIP, getZip());

        map.put(Column.PRODUCT_TAX_CLASS_CODE, getProductTaxClass().getCode());
        map.put(Column.RATE, getRate());
        map.put(Column.LABEL, getLabel());

        return map;
    }
}
