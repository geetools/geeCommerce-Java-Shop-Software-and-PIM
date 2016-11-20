package com.geecommerce.checkout.model;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.xml.bind.annotation.XmlRootElement;

import com.geecommerce.core.service.AbstractModel;
import com.geecommerce.core.service.annotation.Model;
import com.geecommerce.core.type.Id;

@Model("sale_order_shipment_item")
@XmlRootElement(name = "shipment_item")
public class DefaultOrderShipmentItem extends AbstractModel implements OrderShipmentItem {
    private static final long serialVersionUID = -4446294762508347458L;
    private Id id = null;
    private Id shipmentId = null;
    private Id orderItemId = null;
    private int quantity = 0;

    @Override
    public Id getId() {
        return id;
    }

    @Override
    public OrderShipmentItem setId(Id id) {
        this.id = id;
        return this;
    }

    @Override
    public OrderShipmentItem setQuantity(int quantity) {
        this.quantity = quantity;
        return this;
    }

    @Override
    public int getQuantity() {
        return quantity;
    }

    @Override
    public OrderShipmentItem setOrderItemId(Id orderItemId) {
        this.orderItemId = orderItemId;
        return this;
    }

    @Override
    public Id getOrderItemId() {
        return orderItemId;
    }

    @Override
    public OrderShipmentItem belongsTo(OrderShipment orderShipment) {
        shipmentId = orderShipment.getId();
        return this;
    }

    @Override
    public Id getOrderShipmentId() {
        return shipmentId;
    }

    @Override
    public void fromMap(Map<String, Object> map) {
        super.fromMap(map);

        this.id = id_(map.get(Column.ID));
        this.shipmentId = id_(map.get(Column.ORDER_SHIPMENT_ID));
        this.orderItemId = id_(map.get(Column.ORDER_ITEM_ID));
        this.quantity = int_(map.get(Column.QUANTITY));
    }

    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> m = new LinkedHashMap<>(super.toMap());

        m.put(Column.ID, getId());
        m.put(Column.ORDER_SHIPMENT_ID, getOrderShipmentId());
        m.put(Column.ORDER_ITEM_ID, getOrderItemId());
        m.put(Column.QUANTITY, getQuantity());
        return m;
    }
}
