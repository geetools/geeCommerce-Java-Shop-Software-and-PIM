package com.geecommerce.checkout.model;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.geecommerce.core.payment.PaymentStatus;
import com.geecommerce.core.service.api.Model;
import com.geecommerce.core.type.Id;

public interface OrderPayment extends Model {
    public Id getId();

    public OrderPayment setId(Id id);

    public Id getOrderId();

    public OrderPayment setOrderId(Id orderId);

    public String getPaymentMethodCode();

    public OrderPayment setPaymentMethodCode(String paymentMethodCode);

    public String getPaymentMethodLabel();

    public String getTransactionId();

    public OrderPayment setTransactionId(String transactionId);

    public String getAuthorizationId();

    public OrderPayment setAuthorizationId(String authorizationId);

    public Boolean getIsAuthorized();

    public OrderPayment setIsAuthorized(Boolean isAuthorized);

    public PaymentStatus getPaymentStatus();

    public OrderPayment setPaymentStatus(PaymentStatus paymentStatus);

    public PaymentStatus getLastPaymentStatus();

    public OrderPayment setLastPaymentStatus(PaymentStatus lastPaymentStatus);

    public String getCurrency();

    public OrderPayment setCurrency(String currency);

    public double getRateAmount();

    public OrderPayment setRateAmount(double amount);

    public double getPaidAmount();

    public OrderPayment setPaidAmount(Double amount);

    public double getRefundedAmount();

    public OrderPayment setRefundedAmount(Double amount);

    public double getAuthorizedAmount();

    public OrderPayment setAuthorizedAmount(Double amount);

    public Date getPaidOn();

    public OrderPayment setPaidOn(Date paidOn);

    public Date getAuthorizedOn();

    public OrderPayment setAuthorizedOn(Date authorizedOn);

    public Date getModifiedOn();

    public OrderPayment setModifiedOn(Date modifiedOn);

    public Map<String, String> getCustom();

    public OrderPayment setCustom(Map<String, String> custom);

    public OrderPayment belongsTo(Order order);

    public List<OrderPaymentEvent> getPaymentEvents();

    static final class Column {
        public static final String ID = "_id";
        public static final String ORDER_ID = "order_fk";
        public static final String PAYMENT_METHOD_CODE = "payment_method_code";
        public static final String TRANSACTION_ID = "transaction_id";
        public static final String AUTHORIZATION_ID = "authorization_id";
        public static final String IS_AUTHORIZED = "is_authorized";
        public static final String PAYMENT_STATUS = "payment_status";
        public static final String LAST_PAYMENT_STATUS = "last_payment_status";
        public static final String CURRENCY = "currency";
        public static final String RATE_AMOUNT = "rate_amount";
        public static final String PAID_AMOUNT = "paid_amount";
        public static final String REFUNDED_AMOUNT = "refunded_amount";
        public static final String AUTHORIZED_AMOUNT = "authorized_amount";
        public static final String CUSTOM = "custom";
        public static final String PAID_ON = "paid_on";
        public static final String AUTHORIZED_ON = "authorized_on";
    }
}