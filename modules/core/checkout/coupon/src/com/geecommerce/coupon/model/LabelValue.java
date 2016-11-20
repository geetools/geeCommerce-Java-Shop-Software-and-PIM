package com.geecommerce.coupon.model;

import com.geecommerce.core.enums.FrontendInput;
import com.geecommerce.core.type.Id;

public class LabelValue {

    private Id id = null;
    private String label = null;
    private Object value = null;
    private Boolean hasOptions = null;
    private FrontendInput frontendInput = null;

    public Id getId() {
        return id;
    }

    public void setId(Id id) {
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public Boolean getHasOptions() {
        return hasOptions;
    }

    public void setHasOptions(Boolean hasOptions) {
        this.hasOptions = hasOptions;
    }

    public FrontendInput getFrontendInput() {
        return frontendInput;
    }

    public void setFrontendInput(FrontendInput frontendInput) {
        this.frontendInput = frontendInput;
    }

}
