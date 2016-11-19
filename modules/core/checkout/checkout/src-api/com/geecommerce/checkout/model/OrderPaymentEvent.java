package com.geecommerce.checkout.model;

import com.geecommerce.core.payment.PaymentStatus;
import com.geecommerce.core.service.api.Model;
import com.geecommerce.core.system.user.model.User;
import com.geecommerce.core.type.Id;

import java.util.Date;

public interface OrderPaymentEvent extends Model {
    public Id getId();

    public OrderPaymentEvent setId(Id id);

    public Id getOrderId();

    public OrderPaymentEvent setOrderId(Id orderId);

    public String getSuccessMessage();

    public OrderPaymentEvent setSuccessMessage(String successMessage);

    public String getErrorMessage();

    public OrderPaymentEvent setErrorMessage(String errorMessage);

    public String getResponseText();

    public OrderPaymentEvent setResponseText(String responseText);

    public String getRequestText();

    public OrderPaymentEvent setRequestText(String requestText);

    public PaymentStatus getPaymentStatus();

    public OrderPaymentEvent setPaymentStatus(PaymentStatus paymentStatus);

    public PaymentStatus getExpectedPaymentStatus();

    public OrderPaymentEvent setExpectedPaymentStatus(PaymentStatus expectedPaymentStatus);

    public Date getCreatedOn();

    public OrderPaymentEvent setCreatedOn(Date createdOn);

    public OrderPaymentEvent belongsTo(Order order);

    public OrderPaymentEvent setOperator(User operator);

    public User getOperator();

    static final class Column {
	public static final String ID = "_id";
	public static final String ORDER_ID = "order_fk";
	public static final String SUCCESS_MESSAGE = "success_message";
	public static final String ERROR_MESSAGE = "error_message";
	public static final String RESPONSE_TEXT = "response_text";
	public static final String REQUEST_TEXT = "request_text";
	public static final String PAYMENT_STATUS = "payment_status";
	public static final String EXPECTED_PAYMENT_STATUS = "expected_payment_status";
	public static final String CREATED_ON = "cr_on";
	public static final String OPERATOR = "operator";
    }
}
