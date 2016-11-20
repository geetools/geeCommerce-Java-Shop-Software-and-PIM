package com.geecommerce.checkout.model;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlRootElement;

import com.geecommerce.checkout.repository.OrderPaymentEvents;
import com.geecommerce.core.payment.AbstractPaymentMethod;
import com.geecommerce.core.payment.PaymentHelper;
import com.geecommerce.core.payment.PaymentStatus;
import com.geecommerce.core.service.AbstractModel;
import com.geecommerce.core.service.annotation.Model;
import com.geecommerce.core.type.Id;
import com.geecommerce.core.util.Json;
import com.google.inject.Inject;

@Model("sale_order_payment")
@XmlRootElement(name = "payment")
public class DefaultOrderPayment extends AbstractModel implements OrderPayment {
    private static final long serialVersionUID = -7541424169078509389L;
    private Id id = null;
    private Id orderId = null;

    private PaymentStatus paymentStatus = null;
    private PaymentStatus lastPaymentStatus = null;

    private String currency = null;

    private Double rateAmount = null;
    private Double paidAmount = null;
    private Double authorizedAmount = null;
    private Double refundedAmount = null;

    private Boolean isAuthorized = null;
    private String authorizationId = null;
    private String transactionId = null;

    private Date paidOn = null;
    private Date authorizedOn = null;
    private Date modifiedOn = null;

    private String paymentMethodCode = null;
    private Map<String, String> custom = null;

    private String paymentMethodLabel = null;

    private final OrderPaymentEvents orderPaymentEvents;

    @Inject
    public DefaultOrderPayment(OrderPaymentEvents orderPaymentEvents) {
        paidAmount = 0.0;
        refundedAmount = 0.0;
        authorizedAmount = 0.0;
        this.orderPaymentEvents = orderPaymentEvents;
    }

    @Override
    public Id getId() {
        return id;
    }

    @Override
    public OrderPayment setId(Id id) {
        this.id = id;
        return this;
    }

    @Override
    public Id getOrderId() {
        return orderId;
    }

    @Override
    public OrderPayment setOrderId(Id orderId) {
        this.orderId = orderId;
        return this;
    }

    @Override
    public String getPaymentMethodCode() {
        return paymentMethodCode;
    }

    @Override
    public String getPaymentMethodLabel() {
        if (paymentMethodLabel == null) {
            AbstractPaymentMethod paymentMethod = PaymentHelper.findPaymentMethodByCode(getPaymentMethodCode());

            if (paymentMethod != null)
                paymentMethodLabel = paymentMethod.getLabel();
        }

        return paymentMethodLabel;
    }

    @Override
    public OrderPayment setPaymentMethodCode(String paymentMethodCode) {
        this.paymentMethodCode = paymentMethodCode;
        return this;
    }

    @Override
    public String getTransactionId() {
        return transactionId;
    }

    @Override
    public OrderPayment setTransactionId(String transactionId) {
        this.transactionId = transactionId;
        return this;
    }

    @Override
    public String getAuthorizationId() {
        return authorizationId;
    }

    @Override
    public OrderPayment setAuthorizationId(String authorizationId) {
        this.authorizationId = authorizationId;
        return this;
    }

    @Override
    public Boolean getIsAuthorized() {
        return isAuthorized;
    }

    @Override
    public OrderPayment setIsAuthorized(Boolean isAuthorized) {
        this.isAuthorized = isAuthorized;
        return this;
    }

    @Override
    public PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }

    @Override
    public OrderPayment setPaymentStatus(PaymentStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
        return this;
    }

    @Override
    public PaymentStatus getLastPaymentStatus() {
        return lastPaymentStatus;
    }

    @Override
    public OrderPayment setLastPaymentStatus(PaymentStatus lastPaymentStatus) {
        this.lastPaymentStatus = lastPaymentStatus;
        return this;
    }

    @Override
    public String getCurrency() {
        return currency;
    }

    @Override
    public OrderPayment setCurrency(String currency) {
        this.currency = currency;
        return this;
    }

    @Override
    public double getRateAmount() {
        if (rateAmount == null)
            rateAmount = 0.0;
        return rateAmount;
    }

    @Override
    public OrderPayment setRateAmount(double rateAmount) {
        this.rateAmount = rateAmount;
        return this;
    }

    @Override
    public double getPaidAmount() {
        return paidAmount;
    }

    @Override
    public OrderPayment setPaidAmount(Double amount) {
        this.paidAmount = amount;
        return this;
    }

    @Override
    public double getRefundedAmount() {
        return refundedAmount;
    }

    @Override
    public OrderPayment setRefundedAmount(Double amount) {
        this.refundedAmount = amount;
        return this;
    }

    @Override
    public double getAuthorizedAmount() {
        return authorizedAmount;
    }

    @Override
    public OrderPayment setAuthorizedAmount(Double amount) {
        this.authorizedAmount = amount;
        return this;
    }

    @Override
    public Date getModifiedOn() {
        return modifiedOn;
    }

    @Override
    public DefaultOrderPayment setModifiedOn(Date modifiedOn) {
        this.modifiedOn = modifiedOn;
        return this;
    }

    @Override
    public Date getPaidOn() {
        return paidOn;
    }

    @Override
    public OrderPayment setPaidOn(Date paidOn) {
        this.paidOn = paidOn;
        return this;
    }

    @Override
    public Date getAuthorizedOn() {
        return authorizedOn;
    }

    @Override
    public OrderPayment setAuthorizedOn(Date authorizedOn) {
        this.authorizedOn = authorizedOn;
        return this;
    }

    @Override
    public Map<String, String> getCustom() {
        if (custom == null)
            custom = new HashMap<>();
        return custom;
    }

    @SuppressWarnings("unused")
    @Override
    public OrderPayment setCustom(Map<String, String> custom) {
        if (this.custom == null)
            this.custom = new HashMap<>();

        if (custom != null) {
            if (custom == null)
                this.custom = custom;
            else
                this.custom.putAll(custom);
        }
        return this;
    }

    @Override
    public OrderPayment belongsTo(Order order) {
        this.orderId = order.getId();
        return this;
    }

    @Override
    public List<OrderPaymentEvent> getPaymentEvents() {
        return orderPaymentEvents.thatBelongTo(getOrderId());
    }

    @SuppressWarnings("unchecked")
    @Override
    public void fromMap(Map<String, Object> map) {
        super.fromMap(map);

        this.id = id_(map.get(Column.ID));
        this.orderId = id_(map.get(Column.ORDER_ID));
        this.paymentMethodCode = str_(map.get(Column.PAYMENT_METHOD_CODE));
        this.transactionId = str_(map.get(Column.TRANSACTION_ID));
        this.authorizationId = str_(map.get(Column.AUTHORIZATION_ID));
        this.rateAmount = double_(map.get(Column.RATE_AMOUNT), 0);
        this.paidAmount = double_(map.get(Column.PAID_AMOUNT));
        this.authorizedAmount = double_(map.get(Column.AUTHORIZED_AMOUNT));
        this.refundedAmount = double_(map.get(Column.REFUNDED_AMOUNT));
        this.paidOn = date_(map.get(Column.PAID_ON));
        this.authorizedOn = date_(map.get(Column.AUTHORIZED_ON));
        this.paymentStatus = enum_(PaymentStatus.class, map.get(Column.PAYMENT_STATUS));
        this.lastPaymentStatus = enum_(PaymentStatus.class, map.get(Column.PAYMENT_STATUS));
        this.currency = str_(map.get(Column.CURRENCY));
        this.isAuthorized = bool_(map.get(Column.IS_AUTHORIZED));
        custom = new HashMap<>();
        String customStr = str_(map.get(Column.CUSTOM));
        if (customStr != null && !customStr.isEmpty())
            this.custom = Json.fromJson(customStr, custom.getClass());
    }

    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> m = new LinkedHashMap<>(super.toMap());

        m.put(Column.ID, getId());
        m.put(Column.ORDER_ID, getOrderId());
        m.put(Column.PAYMENT_METHOD_CODE, getPaymentMethodCode());
        m.put(Column.AUTHORIZATION_ID, getAuthorizationId());
        m.put(Column.TRANSACTION_ID, getTransactionId());
        m.put(Column.RATE_AMOUNT, getRateAmount());
        m.put(Column.PAID_AMOUNT, getPaidAmount());
        m.put(Column.AUTHORIZED_AMOUNT, getAuthorizedAmount());
        m.put(Column.REFUNDED_AMOUNT, getRefundedAmount());
        m.put(Column.PAID_ON, getPaidOn());
        m.put(Column.AUTHORIZED_ON, getAuthorizedOn());
        m.put(Column.PAYMENT_STATUS, getPaymentStatus() == null ? null : getPaymentStatus().toId());
        m.put(Column.LAST_PAYMENT_STATUS, getLastPaymentStatus() == null ? null : getLastPaymentStatus().toId());
        m.put(Column.CURRENCY, getCurrency());
        m.put(Column.IS_AUTHORIZED, getIsAuthorized());
        if (getCustom() != null && getCustom().size() != 0)
            m.put(Column.CUSTOM, Json.toJson(getCustom()));
        return m;
    }

    @Override
    public String toString() {
        return "DefaultOrderPayment [id=" + id + ", orderId=" + orderId + ", paymentStatus=" + paymentStatus
            + ", lastPaymentStatus=" + lastPaymentStatus + ", currency=" + currency + ", paidAmount=" + paidAmount
            + ", authorizedAmount=" + authorizedAmount + ", refundedAmount=" + refundedAmount + ", isAuthorized="
            + isAuthorized + ", authorizationId=" + authorizationId + ", transactionId=" + transactionId
            + ", paidOn=" + paidOn + ", authorizedOn=" + authorizedOn + ", modifiedOn=" + modifiedOn
            + ", paymentMethodCode=" + paymentMethodCode + ", custom=" + custom + "]";
    }
}
