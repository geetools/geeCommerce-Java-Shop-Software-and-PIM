package com.geecommerce.checkout.repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.geecommerce.checkout.model.Order;
import com.geecommerce.checkout.model.OrderPaymentEvent;
import com.geecommerce.core.service.AbstractRepository;
import com.geecommerce.core.service.annotation.Repository;
import com.geecommerce.core.type.Id;

@Repository
public class DefaultOrderPaymentEvents extends AbstractRepository implements OrderPaymentEvents {
    @Override
    public List<OrderPaymentEvent> thatBelongTo(Id orderId) {
        Map<String, Object> filter = new HashMap<>();
        filter.put(Order.Col.ID, orderId);

        List<OrderPaymentEvent> orderPaymentEvents = find(OrderPaymentEvent.class, filter);

        return orderPaymentEvents;
    }
}
