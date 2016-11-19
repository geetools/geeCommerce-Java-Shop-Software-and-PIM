package com.geecommerce.checkout.service;

import com.geecommerce.checkout.model.Order;
import com.geecommerce.checkout.model.OrderPayment;
import com.geecommerce.checkout.model.OrderPaymentEvent;
import com.geecommerce.checkout.repository.OrderPaymentEvents;
import com.geecommerce.checkout.repository.OrderPayments;
import com.geecommerce.checkout.repository.Orders;
import com.geecommerce.core.App;
import com.geecommerce.core.payment.PaymentEventResponse;
import com.geecommerce.core.payment.PaymentResponse;
import com.geecommerce.core.payment.PaymentStatus;
import com.geecommerce.core.service.annotation.Service;
import com.geecommerce.core.type.Id;
import com.geecommerce.core.util.DateTimes;
import com.google.inject.Inject;

@Service
public class DefaultPaymentService implements PaymentService {
    @Inject
    protected App app;

    protected final Orders orders;
    protected final OrderPayments orderPayments;
    protected final OrderPaymentEvents orderPaymentEvents;

    @Inject
    public DefaultPaymentService(Orders orders, OrderPayments orderPayments, OrderPaymentEvents orderPaymentEvents) {
        this.orders = orders;
        this.orderPayments = orderPayments;
        this.orderPaymentEvents = orderPaymentEvents;
    }

    @Override
    public void processPayment(Id orderId, PaymentResponse response) {
        Order order = orders.findById(Order.class, orderId);
        OrderPayment orderPayment = orderPayments.thatBelongTo(order);
        processPayment(orderPayment, response);
    }

    @Override
    public void processPayment(OrderPayment orderPayment, PaymentResponse response) {
        savePaymentEvent(orderPayment.getOrderId(), response.getPaymentEventResponse());

        // to avoid duplicate subscription events
        if (response.getPaymentEventResponse().getPaymentStatus() == orderPayment.getPaymentStatus())
            return;

        if (response.getPaymentEventResponse().getPaymentStatus() == PaymentStatus.ERROR) {
            orderPayment.setLastPaymentStatus(PaymentStatus.ERROR);
        } else if (response.getPaymentEventResponse().getPaymentStatus() == PaymentStatus.PENDING) {
            orderPayment.setPaymentStatus(PaymentStatus.PENDING);
            orderPayment.setLastPaymentStatus(PaymentStatus.PENDING);

            String paymentMethodCode = orderPayment.getPaymentMethodCode();
            if ("scs_cod".equals(paymentMethodCode) || "scs_cp".equals(paymentMethodCode)) {
                app.publish("order:payment:complete", "orderId", orderPayment.getOrderId(), "code", paymentMethodCode, "status", PaymentStatus.PENDING);
            }

        } else if (response.getPaymentEventResponse().getPaymentStatus() == PaymentStatus.PAID) {
            orderPayment.setPaymentStatus(PaymentStatus.PAID).setLastPaymentStatus(PaymentStatus.PAID).setPaidOn(DateTimes.newDate()).setPaidAmount(response.getAmount())
                .setTransactionId(response.getTransactionId()).setCustom(response.getCustom());

            String paymentMethodCode = orderPayment.getPaymentMethodCode();
            if (!"scs_cod".equals(paymentMethodCode) && !"scs_cp".equals(paymentMethodCode)) {
                app.publish("order:payment:complete", "orderId", orderPayment.getOrderId(), "code", paymentMethodCode, "status", PaymentStatus.PAID);
            }
        } else if (response.getPaymentEventResponse().getPaymentStatus() == PaymentStatus.AUTHORIZED) {
            orderPayment.setPaymentStatus(PaymentStatus.AUTHORIZED).setLastPaymentStatus(PaymentStatus.AUTHORIZED).setAuthorizedOn(DateTimes.newDate()).setAuthorizedAmount(response.getAmount())
                .setAuthorizationId(response.getTransactionId())
                .setCustom(response.getCustom());
        } else if (response.getPaymentEventResponse().getPaymentStatus() == PaymentStatus.CAPTURED) {
            orderPayment.setPaymentStatus(PaymentStatus.CAPTURED).setLastPaymentStatus(PaymentStatus.CAPTURED).setPaidOn(DateTimes.newDate()).setPaidAmount(response.getAmount())
                .setTransactionId(response.getTransactionId())
                .setCustom(response.getCustom());
        } else if (response.getPaymentEventResponse().getPaymentStatus() == PaymentStatus.VOIDED) {
            orderPayment.setPaymentStatus(PaymentStatus.VOIDED).setLastPaymentStatus(PaymentStatus.VOIDED).setCustom(response.getCustom());
        } else if (response.getPaymentEventResponse().getPaymentStatus() == PaymentStatus.REFUNDED) {
            orderPayment.setPaymentStatus(PaymentStatus.REFUNDED).setLastPaymentStatus(PaymentStatus.REFUNDED).setCustom(response.getCustom());
        } else if (response.getPaymentEventResponse().getPaymentStatus() == PaymentStatus.PARTIALLYREFUNDED) {
            orderPayment.setPaymentStatus(PaymentStatus.PARTIALLYREFUNDED).setLastPaymentStatus(PaymentStatus.PARTIALLYREFUNDED)
                .setRefundedAmount(orderPayment.getRefundedAmount() + response.getAmount()).setCustom(response.getCustom());
        }

        orderPayments.update(orderPayment);
    }

    @Override
    public boolean canBePaid(Order order) {
        if (order.getOrderPayment().getPaymentStatus() == null || order.getOrderPayment().getPaymentStatus() == PaymentStatus.PENDING)
            return true;

        return false;
    }

    @Override
    public boolean canBeAuthorized(Order order) {
        if (order.getOrderPayment().getPaymentStatus() == null || order.getOrderPayment().getPaymentStatus() == PaymentStatus.PENDING)
            return true;

        return false;
    }

    @Override
    public boolean canBeCaptured(Order order) {
        if (order.getOrderPayment().getIsAuthorized() != null && order.getOrderPayment().getIsAuthorized() && order.getOrderPayment().getPaymentStatus() == PaymentStatus.AUTHORIZED)
            return true;

        return false;
    }

    @Override
    public boolean canBeVoided(Order order) {
        if (order.getOrderPayment().getIsAuthorized() != null && order.getOrderPayment().getIsAuthorized() && order.getOrderPayment().getPaymentStatus() == PaymentStatus.AUTHORIZED)
            return true;

        return false;
    }

    @Override
    public boolean canBePartiallyRefunded(Order order) {
        PaymentStatus status = order.getOrderPayment().getPaymentStatus();
        if (status == PaymentStatus.PAID || status == PaymentStatus.CAPTURED)
            return true;

        if (status == PaymentStatus.PARTIALLYREFUNDED && order.getOrderPayment().getPaidAmount() > order.getOrderPayment().getRefundedAmount())
            return true;

        return false;
    }

    @Override
    public boolean canBeRefunded(Order order) {
        PaymentStatus status = order.getOrderPayment().getPaymentStatus();
        if (status == PaymentStatus.PAID || status == PaymentStatus.CAPTURED)
            return true;

        return false;
    }

    @Override
    public double possibleRefundAmount(Order order) {
        return order.getOrderPayment().getPaidAmount() - order.getOrderPayment().getRefundedAmount();
    }

    private void savePaymentEvent(Id orderId, PaymentEventResponse paymentEventResponse) {
        OrderPaymentEvent orderPaymentEvent = app.getModel(OrderPaymentEvent.class);

        orderPaymentEvent.setOrderId(orderId).setId(app.nextId()).setSuccessMessage(paymentEventResponse.getSuccessMessage()).setErrorMessage(paymentEventResponse.getErrorMessage())
            .setPaymentStatus(paymentEventResponse.getPaymentStatus())
            .setExpectedPaymentStatus(paymentEventResponse.getExpectedPaymentStatus()).setRequestText(paymentEventResponse.getRequestText()).setResponseText(paymentEventResponse.getResponseText())
            .setCreatedOn(DateTimes.newDate());

        orderPaymentEvents.add(orderPaymentEvent);
    }
}