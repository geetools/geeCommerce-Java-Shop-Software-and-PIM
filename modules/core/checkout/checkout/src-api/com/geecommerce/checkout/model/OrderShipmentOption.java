package com.geecommerce.checkout.model;

import com.geecommerce.core.service.api.Model;
import com.geecommerce.core.type.Id;

public interface OrderShipmentOption extends Model {
    public Id getId();

    public OrderShipmentOption setId(Id id);

    public String getName();

    public OrderShipmentOption setName(String name);

    public String getCarrier();

    public OrderShipmentOption setCarrier(String carrier);

    public String getOption();

    public OrderShipmentOption setOption(String option);

    public double getAmount();

    public OrderShipmentOption setAmount(double amount);

    public OrderShipmentOption belongsTo(OrderShipment order);

    public Id getOrderShipmentId();

    static final class Column {
        public static final String ID = "_id";
        public static final String ORDER_SHIPMENT_ID = "order_shipment_fk";
        public static final String NAME = "name";
        public static final String CARRIER = "carrier";
        public static final String OPTION = "option";
        public static final String AMOUNT = "amount";
    }
}
