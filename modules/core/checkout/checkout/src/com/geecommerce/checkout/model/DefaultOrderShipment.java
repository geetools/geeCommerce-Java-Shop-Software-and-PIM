package com.geecommerce.checkout.model;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlRootElement;

import com.google.inject.Inject;
import com.geecommerce.checkout.repository.OrderShipmentItems;
import com.geecommerce.checkout.repository.OrderShipmentOptions;
import com.geecommerce.core.service.AbstractModel;
import com.geecommerce.core.service.annotation.Model;
import com.geecommerce.core.type.Id;
import com.geecommerce.retail.model.RetailStore;
import com.geecommerce.retail.service.RetailStoreService;

@Model("sale_order_shipment")
@XmlRootElement(name = "shipment")
public class DefaultOrderShipment extends AbstractModel implements OrderShipment {
    private static final long serialVersionUID = 6363285944512290600L;
    private Id id = null;
    private Id orderId = null;
    private Id storeId = null;
    private String carrierCode = null;
    private String trackerCode = null;
    private String optionCode = null;
    private String optionName = null;
    private String trackingNumber = null;
    private Double shippingAmount = null;
    List<OrderShipmentItem> shipmentItems = null;
    private Date shippedOn = null;
    private Date deliveredOn = null;
    private Boolean freeShipping = null;
    private String freeShippingDescription = null;
    private Double deliveryPrice = null;
    private Double postageAndPackingPrice = null;
    List<OrderShipmentOption> shipmentOptions = null;

    private RetailStore retailStore = null;

    private final OrderShipmentOptions orderShipmentOptions;
    private final OrderShipmentItems orderShipmentItems;
    private final RetailStoreService retailStoreService;

    @Inject
    public DefaultOrderShipment(OrderShipmentOptions orderShipmentOptions, OrderShipmentItems orderShipmentItems, RetailStoreService retailStoreService) {
	this.orderShipmentOptions = orderShipmentOptions;
	this.orderShipmentItems = orderShipmentItems;
	this.retailStoreService = retailStoreService;
    }

    @Override
    public Id getId() {
	return id;
    }

    @Override
    public OrderShipment setId(Id id) {
	this.id = id;
	return this;
    }

    @Override
    public Id getOrderId() {
	return orderId;
    }

    @Override
    public String getCarrierCode() {
	return carrierCode;
    }

    @Override
    public OrderShipment setCarrierCode(String carrierCode) {
	this.carrierCode = carrierCode;
	return this;
    }

    @Override
    public String getTrackerCode() {
	return trackerCode;
    }

    @Override
    public OrderShipment setTrackerCode(String trackerCode) {
	this.trackerCode = trackerCode;
	return this;
    }

    @Override
    public String getTrackingNumber() {
	return trackingNumber;
    }

    @Override
    public OrderShipment setTrackingNumber(String trackingNumber) {
	this.trackingNumber = trackingNumber;
	return this;
    }

    @Override
    public String getOptionCode() {
	return optionCode;
    }

    @Override
    public OrderShipment setOptionCode(String optionCode) {
	this.optionCode = optionCode;
	return this;
    }

    @Override
    public String getOptionName() {
	return optionName;
    }

    @Override
    public OrderShipment setOptionName(String optionName) {
	this.optionName = optionName;
	return this;
    }

    @Override
    public Double getShippingAmount() {
	return shippingAmount;
    }

    @Override
    public OrderShipment setPostageAndPackingPrice(Double postageAndPackingPrice) {
	this.postageAndPackingPrice = postageAndPackingPrice;
	return this;
    }

    @Override
    public Double getPostageAndPackingPrice() {
	return postageAndPackingPrice;
    }

    @Override
    public OrderShipment setDeliveryPrice(Double deliveryPrice) {
	this.deliveryPrice = deliveryPrice;
	return this;
    }

    @Override
    public Double getDeliveryPrice() {
	return deliveryPrice;
    }

    @Override
    public OrderShipment setShipmentItems(List<OrderShipmentItem> shipmentItems) {
	this.shipmentItems = shipmentItems;
	return this;
    }

    @Override
    public List<OrderShipmentItem> getShipmentItems() {
	if (shipmentItems == null) {
	    shipmentItems = orderShipmentItems.thatBelongTo(this);
	    if (shipmentItems == null || shipmentItems.size() == 0) {
		shipmentItems = null;
	    }
	}
	return shipmentItems;
    }

    @Override
    public Date getShippedOn() {
	return shippedOn;
    }

    @Override
    public OrderShipment setShippedOn(Date shippedOn) {
	this.shippedOn = shippedOn;
	return this;
    }

    @Override
    public Date getDeliveredOn() {
	return deliveredOn;
    }

    @Override
    public OrderShipment setDeliveredOn(Date deliveredOn) {
	this.deliveredOn = deliveredOn;
	return this;
    }

    @Override
    public Boolean isFreeShipping() {
	return freeShipping;
    }

    @Override
    public OrderShipment setFreeShipping(Boolean freeShipping) {
	this.freeShipping = freeShipping;
	return this;
    }

    @Override
    public String getFreeShippingDescription() {
	return freeShippingDescription;
    }

    @Override
    public OrderShipment setFreeShippingDescription(String description) {
	return null;
    }

    @Override
    public Id getStoreId() {
	return storeId;
    }

    @Override
    public OrderShipment setStoreId(Id storeId) {
	this.storeId = storeId;
	retailStore = null;
	return this;
    }

    @Override
    public RetailStore getStore() {
	if (retailStore == null && storeId != null)
	    retailStore = retailStoreService.getRetailStore(storeId);
	return retailStore;
    }

    @Override
    public List<OrderShipmentOption> getShippingOptions() {
	if (shipmentOptions == null)
	    shipmentOptions = orderShipmentOptions.thatBelongTo(this);
	return shipmentOptions;
    }

    @Override
    public OrderShipment setShippingOptions(List<OrderShipmentOption> shipmentOptions) {
	this.shipmentOptions = shipmentOptions;
	return this;
    }

    @Override
    public OrderShipment setShippingAmount(Double shippingAmount) {
	this.shippingAmount = shippingAmount;
	return this;
    }

    @Override
    public OrderShipment belongsTo(Order order) {
	this.orderId = order.getId();
	return this;
    }

    @Override
    public void fromMap(Map<String, Object> map) {
	super.fromMap(map);

	this.id = id_(map.get(Column.ID));
	this.orderId = id_(map.get(Column.ORDER_ID));
	this.storeId = id_(map.get(Column.STORE_ID));
	this.carrierCode = str_(map.get(Column.CARRIER));
	this.trackerCode = str_(map.get(Column.TRACKER));
	this.optionCode = str_(map.get(Column.OPTION));
	this.optionName = str_(map.get(Column.OPTION_NAME));
	this.trackingNumber = str_(map.get(Column.TRACKING_NUMBER));
	this.shippingAmount = double_(map.get(Column.SHIPPING_AMOUNT));
	this.shippedOn = date_(map.get(Column.SHIPPED_ON));
	this.deliveredOn = date_(map.get(Column.DELIVERED_ON));
	this.freeShipping = bool_(map.get(Column.FREE_SHIPPING));
	this.freeShippingDescription = str_(map.get(Column.FREE_SHIPPING_DESCRIPTION));
	this.deliveryPrice = double_(map.get(Column.DELIVERY_PRICE));
	this.postageAndPackingPrice = double_(map.get(Column.POSTAGE_AND_PACKING_PRICE));
    }

    @Override
    public Map<String, Object> toMap() {
	Map<String, Object> m = new LinkedHashMap<>(super.toMap());

	m.put(Column.ID, getId());
	m.put(Column.ORDER_ID, getOrderId());
	m.put(Column.STORE_ID, getStoreId());
	m.put(Column.CARRIER, getCarrierCode());
	m.put(Column.TRACKER, getTrackerCode());
	m.put(Column.OPTION, getOptionCode());
	m.put(Column.OPTION_NAME, getOptionName());
	m.put(Column.TRACKING_NUMBER, getTrackingNumber());
	m.put(Column.SHIPPING_AMOUNT, getShippingAmount());
	m.put(Column.SHIPPED_ON, getShippedOn());
	m.put(Column.DELIVERED_ON, getDeliveredOn());
	m.put(Column.FREE_SHIPPING, isFreeShipping());
	m.put(Column.FREE_SHIPPING_DESCRIPTION, getFreeShippingDescription());
	m.put(Column.DELIVERY_PRICE, getDeliveryPrice());
	m.put(Column.POSTAGE_AND_PACKING_PRICE, getPostageAndPackingPrice());
	return m;
    }
}
