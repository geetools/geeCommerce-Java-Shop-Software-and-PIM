package com.geecommerce.checkout.model;

import java.util.Map;

import javax.xml.bind.annotation.XmlRootElement;

import com.geecommerce.core.service.AbstractModel;
import com.geecommerce.core.service.annotation.Model;
import com.geecommerce.core.type.Id;

@Model("sale_order_shipment_option")
@XmlRootElement(name = "shipment_option")
public class DefaultOrderShipmentOption extends AbstractModel implements OrderShipmentOption {
    private Id id = null;
    private Id shipmentId = null;
    private String name = null;
    private String carrier = null;
    private String option = null;
    private double amount = 0.0;

    @Override
    public OrderShipmentOption setId(Id id) {
        this.id = id;
        return this;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public OrderShipmentOption setName(String name) {
        this.name = name;
        return this;
    }

    @Override
    public String getCarrier() {
        return carrier;
    }

    @Override
    public OrderShipmentOption setCarrier(String carrier) {
        this.carrier = carrier;
        return this;
    }

    @Override
    public String getOption() {
        return option;
    }

    @Override
    public OrderShipmentOption setOption(String option) {
        this.option = option;
        return this;
    }

    @Override
    public double getAmount() {
        return amount;
    }

    @Override
    public OrderShipmentOption setAmount(double amount) {
        this.amount = amount;
        return this;
    }

    @Override
    public OrderShipmentOption belongsTo(OrderShipment orderShipment) {
        shipmentId = orderShipment.getId();
        return this;
    }

    @Override
    public Id getOrderShipmentId() {
        return shipmentId;
    }

    @Override
    public Id getId() {
        return id;
    }

    @Override
    public void fromMap(Map<String, Object> map) {
        this.id = id_(map.get(Column.ID));
        this.shipmentId = id_(map.get(Column.ORDER_SHIPMENT_ID));
        this.name = str_(map.get(Column.NAME));
        this.carrier = str_(map.get(Column.CARRIER));
        this.option = str_(map.get(Column.OPTION));
        this.amount = double_(map.get(Column.AMOUNT), 0.0);
    }

    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> m = super.toMap();
        m.put(Column.ID, getId());
        m.put(Column.ORDER_SHIPMENT_ID, getOrderShipmentId());
        m.put(Column.NAME, getName());
        m.put(Column.CARRIER, getCarrier());
        m.put(Column.OPTION, getOption());
        m.put(Column.AMOUNT, getAmount());
        return m;
    }
}
