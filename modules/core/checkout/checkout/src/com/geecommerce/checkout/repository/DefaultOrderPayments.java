package com.geecommerce.checkout.repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.geecommerce.checkout.model.Order;
import com.geecommerce.checkout.model.OrderPayment;
import com.geecommerce.core.service.AbstractRepository;
import com.geecommerce.core.service.annotation.Repository;

@Repository
public class DefaultOrderPayments extends AbstractRepository implements OrderPayments {
    @Override
    public OrderPayment thatBelongTo(Order order) {
        Map<String, Object> filter = new HashMap<>();
        filter.put(OrderPayment.Column.ORDER_ID, order.getId());

        List<OrderPayment> orderPayments = find(OrderPayment.class, filter);

        return orderPayments == null || orderPayments.isEmpty() ? null : orderPayments.get(0);
    }
}
