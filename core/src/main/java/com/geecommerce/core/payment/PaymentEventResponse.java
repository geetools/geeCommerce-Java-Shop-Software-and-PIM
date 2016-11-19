package com.geecommerce.core.payment;

public class PaymentEventResponse {
    private PaymentStatus paymentStatus = null;
    private PaymentStatus expectedPaymentStatus = null;
    private String successMessage = null;
    private String errorMessage = null;
    private String responseText = null;
    private String requestText = null;

    public PaymentEventResponse() {

    }

    public PaymentEventResponse(PaymentStatus paymentStatus, PaymentStatus expectedPaymentStatus, String successMessage, String errorMessage, String responseText, String requestText) {
	super();
	this.paymentStatus = paymentStatus;
	this.expectedPaymentStatus = expectedPaymentStatus;
	this.successMessage = successMessage;
	this.errorMessage = errorMessage;
	this.responseText = responseText;
	this.requestText = requestText;
    }

    public String getRequestText() {
	return requestText;
    }

    public void setRequestText(String requestText) {
	this.requestText = requestText;
    }

    public PaymentStatus getPaymentStatus() {
	return paymentStatus;
    }

    public void setPaymentStatus(PaymentStatus paymentStatus) {
	this.paymentStatus = paymentStatus;
    }

    public PaymentStatus getExpectedPaymentStatus() {
	return expectedPaymentStatus;
    }

    public void setExpectedPaymentStatus(PaymentStatus expectedPaymentStatus) {
	this.expectedPaymentStatus = expectedPaymentStatus;
    }

    public String getSuccessMessage() {
	return successMessage;
    }

    public void setSuccessMessage(String successMessage) {
	this.successMessage = successMessage;
    }

    public String getErrorMessage() {
	return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
	this.errorMessage = errorMessage;
    }

    public String getResponseText() {
	return responseText;
    }

    public void setResponseText(String responseText) {
	this.responseText = responseText;
    }
}
