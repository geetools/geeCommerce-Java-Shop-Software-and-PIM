package com.geecommerce.checkout.repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.geecommerce.checkout.model.OrderShipment;
import com.geecommerce.checkout.model.OrderShipmentItem;
import com.geecommerce.core.service.AbstractRepository;
import com.geecommerce.core.service.annotation.Repository;

@Repository
public class DefaultOrderShipmentItems extends AbstractRepository implements OrderShipmentItems {
    @Override
    public List<OrderShipmentItem> thatBelongTo(OrderShipment orderShipment) {
        Map<String, Object> filter = new HashMap<>();
        filter.put(OrderShipmentItem.Column.ORDER_SHIPMENT_ID, orderShipment.getId());
        List<OrderShipmentItem> orderShipmentItems = find(OrderShipmentItem.class, filter);
        return orderShipmentItems;
    }
}
