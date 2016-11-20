package com.geecommerce.core.payment;

import java.util.HashMap;
import java.util.Map;

public class PaymentResponse {
    private PaymentEventResponse paymentEventResponse;
    private Double amount = null;
    private String url = null;
    private String transactionId = null;
    private Map<String, String> custom = null;

    public PaymentResponse() {
        custom = new HashMap<>();
    }

    public PaymentResponse(PaymentEventResponse paymentEventResponse, Double amount, String url, String transactionId,
        Map<String, String> custom) {
        super();
        this.paymentEventResponse = paymentEventResponse;
        this.amount = amount;
        this.url = url;
        this.transactionId = transactionId;
        this.custom = custom;
    }

    public PaymentEventResponse getPaymentEventResponse() {
        return paymentEventResponse;
    }

    public void setPaymentEventResponse(PaymentEventResponse paymentEventResponse) {
        this.paymentEventResponse = paymentEventResponse;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public Map<String, String> getCustom() {
        return custom;
    }

    public void setCustom(Map<String, String> custom) {
        this.custom = custom;
    }
}
