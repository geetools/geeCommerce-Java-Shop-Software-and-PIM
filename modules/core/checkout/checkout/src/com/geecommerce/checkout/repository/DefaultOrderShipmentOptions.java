package com.geecommerce.checkout.repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.geecommerce.checkout.model.OrderShipment;
import com.geecommerce.checkout.model.OrderShipmentItem;
import com.geecommerce.checkout.model.OrderShipmentOption;
import com.geecommerce.core.service.AbstractRepository;
import com.geecommerce.core.service.annotation.Repository;

@Repository
public class DefaultOrderShipmentOptions extends AbstractRepository implements OrderShipmentOptions {
    @Override
    public List<OrderShipmentOption> thatBelongTo(OrderShipment orderShipment) {
        Map<String, Object> filter = new HashMap<>();
        filter.put(OrderShipmentItem.Column.ORDER_SHIPMENT_ID, orderShipment.getId());
        List<OrderShipmentOption> orderShipmentOptions = find(OrderShipmentOption.class, filter);
        return orderShipmentOptions;
    }
}
