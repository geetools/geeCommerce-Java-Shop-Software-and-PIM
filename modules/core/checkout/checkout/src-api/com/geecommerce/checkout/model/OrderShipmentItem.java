package com.geecommerce.checkout.model;

import com.geecommerce.core.service.api.Model;
import com.geecommerce.core.type.Id;

public interface OrderShipmentItem extends Model {

    public OrderShipmentItem setId(Id id);

    public Id getId();

    public OrderShipmentItem setQuantity(int quantity);

    public int getQuantity();

    public OrderShipmentItem setOrderItemId(Id orderItemId);

    public Id getOrderItemId();

    public OrderShipmentItem belongsTo(OrderShipment order);

    public Id getOrderShipmentId();

    static final class Column {
        public static final String ID = "_id";
        public static final String ORDER_SHIPMENT_ID = "order_shipment_fk";
        public static final String ORDER_ITEM_ID = "order_item_fk";
        public static final String QUANTITY = "quantity";
    }
}
