package com.geecommerce.checkout.model;

import java.util.Date;
import java.util.List;

import com.geecommerce.core.service.api.Model;
import com.geecommerce.core.type.Id;
import com.geecommerce.retail.model.RetailStore;

public interface OrderShipment extends Model {
    public Id getId();

    public OrderShipment setId(Id id);

    public Id getOrderId();

    public String getCarrierCode();

    public OrderShipment setCarrierCode(String carrierCode);

    public String getTrackerCode();

    public OrderShipment setTrackerCode(String trackerCode);

    public String getTrackingNumber();

    public OrderShipment setTrackingNumber(String trackingNumber);

    public String getOptionCode();

    public OrderShipment setOptionCode(String optionCode);

    public String getOptionName();

    public OrderShipment setOptionName(String optionName);

    public OrderShipment belongsTo(Order order);

    public OrderShipment setShippingAmount(Double shippingAmount);

    public Double getShippingAmount();

    public OrderShipment setPostageAndPackingPrice(Double postageAndPackingPrice);

    public Double getPostageAndPackingPrice();

    public OrderShipment setDeliveryPrice(Double deliveryPrice);

    public Double getDeliveryPrice();

    public OrderShipment setShipmentItems(List<OrderShipmentItem> shipmentItems);

    public List<OrderShipmentItem> getShipmentItems();

    public Date getShippedOn();

    public OrderShipment setShippedOn(Date shippedOn);

    public Date getDeliveredOn();

    public OrderShipment setDeliveredOn(Date deliveredOn);

    public Boolean isFreeShipping();

    public OrderShipment setFreeShipping(Boolean freeShipping);

    public String getFreeShippingDescription();

    public OrderShipment setFreeShippingDescription(String description);

    public Id getStoreId();

    public OrderShipment setStoreId(Id storeId);

    public RetailStore getStore();

    public List<OrderShipmentOption> getShippingOptions();

    public OrderShipment setShippingOptions(List<OrderShipmentOption> orderShipmentOptions);

    static final class Column {
        public static final String ID = "_id";
        public static final String ORDER_ID = "order_fk";
        public static final String CARRIER = "carrier";
        public static final String TRACKER = "tracker";
        public static final String OPTION = "option";
        public static final String OPTION_NAME = "option_name";
        public static final String TRACKING_NUMBER = "tracking_number";
        public static final String SHIPPING_AMOUNT = "shipping_amount";
        public static final String SHIPPED_ON = "shipped_on";
        public static final String DELIVERED_ON = "delivered_on";
        public static final String FREE_SHIPPING = "free_shipping";
        public static final String FREE_SHIPPING_DESCRIPTION = "free_shipping_descr";
        public static final String STORE_ID = "store_id";
        public static final String DELIVERY_PRICE = "delivery_price";
        public static final String POSTAGE_AND_PACKING_PRICE = "postage_and_packing_price";
        public static final String OPTIONS = "options";
    }
}
