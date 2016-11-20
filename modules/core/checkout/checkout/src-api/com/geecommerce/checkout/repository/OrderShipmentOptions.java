package com.geecommerce.checkout.repository;

import java.util.List;

import com.geecommerce.checkout.model.OrderShipment;
import com.geecommerce.checkout.model.OrderShipmentOption;
import com.geecommerce.core.service.api.Repository;

public interface OrderShipmentOptions extends Repository {
    public List<OrderShipmentOption> thatBelongTo(OrderShipment orderShipment);
}
