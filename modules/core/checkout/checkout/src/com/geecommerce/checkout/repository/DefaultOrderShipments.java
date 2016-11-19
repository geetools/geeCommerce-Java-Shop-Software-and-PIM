package com.geecommerce.checkout.repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.geecommerce.checkout.model.Order;
import com.geecommerce.checkout.model.OrderShipment;
import com.geecommerce.core.service.AbstractRepository;
import com.geecommerce.core.service.annotation.Repository;

@Repository
public class DefaultOrderShipments extends AbstractRepository implements OrderShipments {
    @Override
    public List<OrderShipment> thatBelongTo(Order order) {
        Map<String, Object> filter = new HashMap<>();
        filter.put(OrderShipment.Column.ORDER_ID, order.getId());
        List<OrderShipment> orderShipments = find(OrderShipment.class, filter);
        return orderShipments;
    }
}
