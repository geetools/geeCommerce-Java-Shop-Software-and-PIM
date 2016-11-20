package com.geecommerce.checkout.repository;

import java.util.List;

import com.geecommerce.checkout.model.OrderShipment;
import com.geecommerce.checkout.model.OrderShipmentItem;
import com.geecommerce.core.service.api.Repository;

public interface OrderShipmentItems extends Repository {
    public List<OrderShipmentItem> thatBelongTo(OrderShipment orderShipment);
}
