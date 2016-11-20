package com.geecommerce.checkout.repository;

import java.util.List;

import com.geecommerce.checkout.model.OrderPaymentEvent;
import com.geecommerce.core.service.api.Repository;
import com.geecommerce.core.type.Id;

public interface OrderPaymentEvents extends Repository {
    public List<OrderPaymentEvent> thatBelongTo(Id orderId);
}
