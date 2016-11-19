package com.geecommerce.checkout.repository;

import com.geecommerce.checkout.model.Order;
import com.geecommerce.checkout.model.OrderPayment;
import com.geecommerce.checkout.model.OrderPaymentEvent;
import com.geecommerce.core.service.api.Repository;
import com.geecommerce.core.type.Id;

import java.util.List;

public interface OrderPaymentEvents extends Repository {
    public List<OrderPaymentEvent> thatBelongTo(Id orderId);
}
