package com.geecommerce.checkout.service;

import com.geecommerce.checkout.model.Checkout;
import com.geecommerce.checkout.model.Order;
import com.geecommerce.checkout.model.OrderPayment;
import com.geecommerce.core.payment.PaymentResponse;
import com.geecommerce.core.type.Id;
import com.geecommerce.inventory.exception.QuantityNotAvailableException;

public interface PaymentService {

    public void processPayment(Id orderId, PaymentResponse response);

    public void processPayment(OrderPayment orderPayment, PaymentResponse response);

    public boolean canBePaid(Order order);

    public boolean canBeAuthorized(Order order);

    public boolean canBeCaptured(Order order);

    public boolean canBeVoided(Order order);

    public boolean canBePartiallyRefunded(Order order);

    public boolean canBeRefunded(Order order);

    public double possibleRefundAmount(Order order);

}
