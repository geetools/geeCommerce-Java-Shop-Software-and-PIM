package com.geecommerce.customer.model;

import com.geecommerce.core.service.AbstractModel;
import com.geecommerce.core.service.annotation.Model;
import com.geecommerce.core.type.Id;

import java.util.*;

@Model("customer_payments")
public class DefaultPayment extends AbstractModel implements Payment {
    private static final long serialVersionUID = 3053074002933339458L;
    private Id id = null;
    private Id customerId = null;
    private String paymentCode = null;
    private Map<String, String> parameters;
    private boolean defaultPayment = false;

    @Override
    public Id getId() {
        return id;
    }

    @Override
    public Payment setId(Id id) {
        this.id = id;
        return this;
    }

    @Override
    public Id getCustomerId() {
        return customerId;
    }

    @Override
    public Payment belongsTo(Customer customer) {
        if (customer == null || customer.getId() == null)
            throw new NullPointerException("The customerId cannot be null");

        this.customerId = customer.getId();
        return this;
    }

    @Override
    public boolean isDefaultPayment() {
        return defaultPayment;
    }

    @Override
    public Payment setDefaultPayment(boolean defaultPayment) {
        this.defaultPayment = defaultPayment;
        return this;
    }

    @Override
    public String getPaymentCode() {
        return paymentCode;
    }

    @Override
    public Payment setPaymentCode(String paymentCode) {
        this.paymentCode = paymentCode;
        return this;
    }

    @Override
    public Map<String, String> getParameters() {
        return parameters;
    }

    @Override
    public Payment setParameters(Map<String, String> parameters) {
        this.parameters = parameters;
        return this;
    }

    @Override
    public void fromMap(Map<String, Object> map) {
        this.id = id_(map.get(Column.ID));
        this.customerId = id_(map.get(Column.CUSTOMER_ID));
        this.paymentCode = str_(map.get(Column.PAYMENT_CODE));
        this.defaultPayment = bool_(map.get(Column.DEFAULT_PAYMENT)) == null ? false : bool_(map.get(Column.DEFAULT_PAYMENT));
        this.parameters = map_(map.get(Column.PARAMETERS));
    }

    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put(Column.ID, getId());
        m.put(Column.CUSTOMER_ID, getCustomerId());
        m.put(Column.PAYMENT_CODE, getPaymentCode());
        m.put(Column.DEFAULT_PAYMENT, isDefaultPayment());
        m.put(Column.PARAMETERS, getParameters());
        return m;
    }

    @Override
    public String toString() {
        return "DefaultPayment [id=" + id + ", customerId=" + customerId + ", paymentCode=" + paymentCode
                + ", isDefaultPayment=" + defaultPayment + ", parameters=" + parameters + "]";
    }
}
