package com.geecommerce.checkout.model;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import com.google.inject.Inject;
import com.geecommerce.core.payment.PaymentStatus;
import com.geecommerce.core.service.AbstractModel;
import com.geecommerce.core.service.annotation.Model;
import com.geecommerce.core.system.user.model.User;
import com.geecommerce.core.system.user.service.UserService;
import com.geecommerce.core.type.Id;

@Model("sale_order_payment_event")
public class DefaultOrderPaymentEvent extends AbstractModel implements OrderPaymentEvent {
    private static final long serialVersionUID = 5970073629771465130L;
    private Id id = null;
    private Id orderId = null;
    private PaymentStatus paymentStatus = null;
    private PaymentStatus expectedPaymentStatus = null;
    private String successMessage = null;
    private String errorMessage = null;
    private String responseText = null;
    private String requestText = null;
    private Date createdOn = null;
    private Id operatorId = null;
    private User operator = null;

    private final UserService userService;

    @Inject
    public DefaultOrderPaymentEvent(UserService userService) {
	this.userService = userService;
    }

    @Override
    public Id getId() {
	return id;
    }

    @Override
    public OrderPaymentEvent setId(Id id) {
	this.id = id;
	return this;
    }

    @Override
    public Id getOrderId() {
	return orderId;
    }

    @Override
    public OrderPaymentEvent setOrderId(Id orderId) {
	this.orderId = orderId;
	return this;
    }

    @Override
    public String getSuccessMessage() {
	return successMessage;
    }

    @Override
    public OrderPaymentEvent setSuccessMessage(String successMessage) {
	this.successMessage = successMessage;
	return this;
    }

    @Override
    public String getErrorMessage() {
	return errorMessage;
    }

    @Override
    public OrderPaymentEvent setErrorMessage(String errorMessage) {
	this.errorMessage = errorMessage;
	return this;
    }

    @Override
    public String getResponseText() {
	return responseText;
    }

    @Override
    public OrderPaymentEvent setResponseText(String responseText) {
	this.responseText = responseText;
	return this;
    }

    @Override
    public String getRequestText() {
	return requestText;
    }

    @Override
    public OrderPaymentEvent setRequestText(String requestText) {
	this.requestText = requestText;
	return this;
    }

    @Override
    public PaymentStatus getPaymentStatus() {
	return paymentStatus;
    }

    @Override
    public OrderPaymentEvent setPaymentStatus(PaymentStatus paymentStatus) {
	this.paymentStatus = paymentStatus;
	return this;
    }

    @Override
    public PaymentStatus getExpectedPaymentStatus() {
	return expectedPaymentStatus;
    }

    @Override
    public OrderPaymentEvent setExpectedPaymentStatus(PaymentStatus expectedPaymentStatus) {
	this.expectedPaymentStatus = expectedPaymentStatus;
	return this;
    }

    @Override
    public Date getCreatedOn() {
	return createdOn;
    }

    @Override
    public OrderPaymentEvent setCreatedOn(Date createdOn) {
	this.createdOn = createdOn;
	return this;
    }

    @Override
    public OrderPaymentEvent belongsTo(Order order) {
	this.orderId = order.getId();
	return this;
    }

    @Override
    public OrderPaymentEvent setOperator(User operator) {
	if (operator == null) {
	    this.operatorId = null;
	    this.operator = null;
	} else {
	    this.operatorId = operator.getId();
	    this.operator = operator;
	}
	return this;
    }

    @Override
    public User getOperator() {
	if (operator == null && operatorId != null) {
	    operator = userService.getUser(operatorId);
	}
	return operator;
    }

    @Override
    public void fromMap(Map<String, Object> map) {
	super.fromMap(map);

	this.id = id_(map.get(Column.ID));
	this.orderId = id_(map.get(Column.ORDER_ID));
	this.responseText = str_(map.get(Column.RESPONSE_TEXT));
	this.requestText = str_(map.get(Column.REQUEST_TEXT));
	this.paymentStatus = PaymentStatus.fromId(int_(map.get(Column.PAYMENT_STATUS)));
	this.expectedPaymentStatus = PaymentStatus.fromId(int_(map.get(Column.EXPECTED_PAYMENT_STATUS)));
	this.successMessage = str_(map.get(Column.SUCCESS_MESSAGE));
	this.errorMessage = str_(map.get(Column.ERROR_MESSAGE));
	this.createdOn = date_(createdOn);
	this.operatorId = id_(map.get(Column.OPERATOR));
    }

    @Override
    public Map<String, Object> toMap() {
	Map<String, Object> m = new LinkedHashMap<>(super.toMap());

	m.put(Column.ID, getId());
	m.put(Column.ORDER_ID, getOrderId());
	m.put(Column.REQUEST_TEXT, getRequestText());
	m.put(Column.RESPONSE_TEXT, getResponseText());
	m.put(Column.SUCCESS_MESSAGE, getSuccessMessage());
	m.put(Column.ERROR_MESSAGE, getErrorMessage());
	m.put(Column.PAYMENT_STATUS, getPaymentStatus() == null ? null : getPaymentStatus().toId());
	m.put(Column.EXPECTED_PAYMENT_STATUS, getExpectedPaymentStatus() == null ? null : getExpectedPaymentStatus().toId());
	m.put(Column.CREATED_ON, getCreatedOn());
	m.put(Column.OPERATOR, operatorId);

	return m;
    }
}
