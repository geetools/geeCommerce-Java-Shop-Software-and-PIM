package com.geecommerce.checkout.pm.cp;

import java.util.Map;

import com.geecommerce.checkout.model.Order;
import com.geecommerce.checkout.pm.cp.configuration.Key;
import com.geecommerce.core.App;
import com.geecommerce.core.payment.AbstractPaymentMethod;
import com.geecommerce.core.payment.PaymentEventResponse;
import com.geecommerce.core.payment.PaymentResponse;
import com.geecommerce.core.payment.PaymentStatus;
import com.geecommerce.core.payment.annotation.PaymentMethod;
import com.google.inject.Inject;

@PaymentMethod
public class CashPayment extends AbstractPaymentMethod {
    @Inject
    protected App app;

    @Override
    public int getSortIndex() {
        return app.cpInt_(Key.SORT_INDEX, 2);
    }

    @Override
    public String getProvider() {
        return "SCS";
    }

    @Override
    public String getCode() {
        return "scs_cp";
    }

    @Override
    public String getLabel() {
        return app.message("Cash Payment");
    }

    @Override
    public String getName() {
        return "Cash Payment";
    }

    @Override
    public String getFormFieldPrefix() {
        return "payment_cp_";
    }

    @Override
    public boolean isFormDataValid(Map<String, Object> formData) {
        // Check fields before sending them to provider
        return true;
    }

    @Override
    public PaymentResponse processPayment(Map<String, Object> formData, Object... data) {
        Order order = (Order) data[0];

        PaymentEventResponse eventResponse = new PaymentEventResponse(PaymentStatus.PENDING, PaymentStatus.PENDING,
            "ok", null, " ", " ");

        PaymentResponse response = new PaymentResponse(eventResponse, order.getTotalAmount(), null, null, null);
        // Send form to provider
        return response;
    }

    @Override
    public PaymentResponse authorizePayment(Map<String, Object> formData, Object... data) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PaymentResponse refundPayment(Map<String, Object> formData, Object... data) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PaymentResponse partiallyRefundPayment(Map<String, Object> formData, Object... data) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PaymentResponse capturePayment(Map<String, Object> formData, Object... data) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PaymentResponse voidPaymentPayment(Map<String, Object> formData, Object... data) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean supportAuthorization() {
        return false;
    }

    @Override
    public boolean supportRefund() {
        return false;
    }

    @Override
    public boolean supportPartiallyRefund() {
        return false;
    }

    @Override
    public boolean supportCapture() {
        return false;
    }

    @Override
    public boolean supportVoid() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return app.cpBool_(Key.ENABLED, false);
    }
}
