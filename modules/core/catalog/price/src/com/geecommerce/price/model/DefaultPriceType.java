package com.geecommerce.price.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import com.geecommerce.core.service.AbstractMultiContextModel;
import com.geecommerce.core.service.annotation.Cacheable;
import com.geecommerce.core.service.annotation.Column;
import com.geecommerce.core.service.annotation.Model;
import com.geecommerce.core.type.ContextObject;
import com.geecommerce.core.type.Id;

@Cacheable
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "priceType")
@Model(collection = "price_types", fieldAccess = true)
public class DefaultPriceType extends AbstractMultiContextModel implements PriceType {
    private static final long serialVersionUID = 90399598657590579L;

    @Column(Col.ID)
    private Id id = null;

    @Column(Col.ID2)
    private String id2 = null;

    @Column(Col.CODE)
    private String code = null;

    @Column(Col.ERP_CODE)
    private String erpCode = null;

    @Column(Col.LABEL)
    private ContextObject<String> label = null;

    @Column(Col.PRIORITY)
    private int priority = 99;

    @Column(Col.ENABLED)
    private boolean enabled = false;

    @Override
    public Id getId() {
        return id;
    }

    @Override
    public PriceType setId(Id id) {
        this.id = id;
        return this;
    }

    @Override
    public String getId2() {
        return id2;
    }

    @Override
    public PriceType setId2(String id2) {
        this.id2 = id2;
        return this;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public PriceType setCode(String code) {
        this.code = code;
        return this;
    }

    @Override
    public String getErpCode() {
        return erpCode;
    }

    @Override
    public PriceType setErpCode(String erpCode) {
        this.erpCode = erpCode;
        return this;
    }

    @Override
    public ContextObject<String> getLabel() {
        return label;
    }

    @Override
    public PriceType setLabel(ContextObject<String> label) {
        this.label = label;
        return this;
    }

    @Override
    public int getPriority() {
        return priority;
    }

    @Override
    public PriceType setPriority(int priority) {
        this.priority = priority;
        return this;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public String toString() {
        return "DefaultPriceType [id=" + id + ", id2=" + id2 + ", code=" + code + ", erpCode=" + erpCode + ", label="
            + label + ", priority=" + priority + ", enabled=" + enabled + "]";
    }
}
