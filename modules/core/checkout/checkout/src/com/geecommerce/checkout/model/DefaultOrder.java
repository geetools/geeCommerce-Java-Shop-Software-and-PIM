package com.geecommerce.checkout.model;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.codehaus.jettison.json.JSONException;

import com.geecommerce.calculation.model.CalculationResult;
import com.geecommerce.checkout.enums.AddressType;
import com.geecommerce.checkout.enums.OrderStatus;
import com.geecommerce.checkout.repository.Checkouts;
import com.geecommerce.checkout.repository.OrderAddresses;
import com.geecommerce.checkout.repository.OrderItems;
import com.geecommerce.checkout.repository.OrderPayments;
import com.geecommerce.checkout.repository.OrderShipments;
import com.geecommerce.checkout.repository.OrderStatusHistories;
import com.geecommerce.core.service.AbstractModel;
import com.geecommerce.core.service.annotation.Column;
import com.geecommerce.core.service.annotation.Model;
import com.geecommerce.core.system.model.RequestContext;
import com.geecommerce.core.system.user.model.User;
import com.geecommerce.core.system.user.service.UserService;
import com.geecommerce.core.type.Id;
import com.geecommerce.core.util.Json;
import com.geecommerce.coupon.model.CouponCode;
import com.geecommerce.coupon.service.CouponService;
import com.geecommerce.customer.model.Customer;
import com.geecommerce.customer.repository.Customers;
import com.google.inject.Inject;
import com.owlike.genson.annotation.JsonIgnore;
import com.sun.xml.txw2.annotation.XmlAttribute;

@Model("sale_order")
@XmlRootElement(name = "order")
@XmlAccessorType(XmlAccessType.NONE)
public class DefaultOrder extends AbstractModel implements Order {
    private static final long serialVersionUID = -7126641007612773030L;

    @Column(Col.ID)
    private Id id = null;

    @Column(Col.ID2)
    private String id2 = null;

    @Column(Col.REQUEST_CONTEXT_ID)
    private Id requestContextId = null;

    @Column(Col.CHECKOUT_ID)
    private Id checkoutId = null;

    @Column(Col.CUSTOMER_ID)
    private Id customerId = null;

    @Column(Col.TOTAL_AMOUNT)
    private Double totalAmount = null;

    @Column(Col.OPERATOR)
    private Id operatorId = null;

    @Column(Col.ORDER_NUMBER)
    private String orderNumber = null;

    @Column(Col.NOTE)
    private String note = null;

    @Column(Col.NOTE_INTERNAL)
    private String noteInternal = null;

    @Column(Col.NOTE_SELLER)
    private String noteSeller = null;

    @Column(Col.ORDER_STATUS)
    private OrderStatus orderStatus = null;

    @Column(Col.DISCOUNT_CODE)
    private String discountCode = null;

    @Column(Col.DISCOUNT_AMOUNT)
    private Double discountAmount = null;

    @Column(Col.DISCOUNT_DESCRIPTION)
    private String discountDescription = null;

    @Column(Col.GIFT_DESCRIPTION)
    private String giftDescription = null;

    @Column(Col.LANGUAGE)
    private String language = null;

    @Column(Col.CANCELED)
    private Boolean canceled = null;

    private List<OrderItem> orderItemList = new ArrayList<>();

    private CalculationResult calculationResult = null;
    private String jsonCalculationResult = null;

    private List<OrderStatusHistory> orderStatusHistoryList = null;

    private OrderAddress deliveryOrderAddress = null;
    private OrderAddress invoiceOrderAddress = null;
    private OrderPayment orderPayment = null;
    private List<OrderShipment> orderShipmentList = null;
    private CouponCode couponCode = null;
    private Id couponCodeId = null;
    private User operator = null;

    // Checkout repository
    private transient final Customers customers;
    private Customer customer;

    // Checkout repository
    private transient final Checkouts checkouts;
    private Checkout checkout;

    private transient final OrderAddresses orderAddresses;
    private transient final UserService userService;
    private transient final OrderItems orderItems;
    private transient final OrderPayments orderPayments;
    private transient final OrderShipments orderShipments;
    private transient final OrderStatusHistories orderStatusHistories;

    @Inject
    public DefaultOrder(Customers customers, Checkouts checkouts, OrderAddresses orderAddresses, OrderItems orderItems,
        OrderPayments orderPayments, OrderShipments orderShipments, UserService userService,
        OrderStatusHistories orderStatusHistories) {
        this.customers = customers;
        this.checkouts = checkouts;
        this.orderAddresses = orderAddresses;
        this.userService = userService;
        this.orderItems = orderItems;
        this.orderPayments = orderPayments;
        this.orderShipments = orderShipments;
        this.orderStatusHistories = orderStatusHistories;
    }

    @Override
    @XmlAttribute
    public Id getId() {
        return id;
    }

    @Override
    public Order setId(Id id) {
        this.id = id;
        return this;
    }

    @Override
    public String getId2() {
        return id2;
    }

    @Override
    public Order setId2(String id2) {
        this.id2 = id2;
        return this;
    }

    @Override
    @XmlAttribute
    public Id getRequestContextId() {
        return requestContextId;
    }

    @Override
    public Order setRequestContextId(Id requestContextId) {
        this.requestContextId = requestContextId;
        return this;
    }

    @Override
    public Order fromRequestContext(RequestContext requestContext) {
        if (requestContext == null || requestContext.getId() == null)
            throw new IllegalStateException("RequestContext cannot be null");

        this.requestContextId = requestContext.getId();
        return this;
    }

    @Override
    @XmlAttribute
    public Id getCheckoutId() {
        return checkoutId;
    }

    @Override
    public Order setCheckoutId(Id checkoutId) {
        this.checkoutId = checkoutId;
        return this;
    }

    @Override
    public Order fromCheckout(Checkout checkout) {
        if (checkout == null || checkout.getId() == null)
            throw new IllegalStateException("Checkout cannot be null");

        this.checkoutId = checkout.getId();
        return this;
    }

    @Override
    @XmlAttribute
    public Id getCustomerId() {
        return customerId;
    }

    @Override
    public Order setCustomerId(Id customerId) {
        this.customerId = customerId;
        return this;
    }

    @Override
    @XmlAttribute
    public Double getTotalAmount() {
        return totalAmount;
    }

    @Override
    public Order setTotalAmount(Double totalAmount) {
        this.totalAmount = totalAmount;
        return this;
    }

    @Override
    public OrderStatus getOrderStatus() {
        return orderStatus;
    }

    @Override
    public Order setOrderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
        return this;
    }

    @Override
    public Order belongsTo(Customer customer) {
        this.customerId = customer.getId();
        return this;
    }

    @Override
    public List<OrderItem> getOrderItems() {
        if (orderItemList.isEmpty()) {
            orderItemList = orderItems.thatBelongTo(this);
        }

        return orderItemList;
    }

    @Override
    public Order setOrderItems(List<OrderItem> orderItems) {
        this.orderItemList = orderItems;
        return this;
    }

    @Override
    public Order addOrderItem(OrderItem orderItem) {
        this.orderItemList.add(orderItem);
        return this;
    }

    @Override
    public OrderAddress getDeliveryOrderAddress() {
        if (deliveryOrderAddress == null) {
            List<OrderAddress> orderAddressList = orderAddresses.thatBelongTo(this);
            for (OrderAddress orderAddress : orderAddressList) {
                if (orderAddress.getAddressType() == AddressType.DELIVERY) {
                    deliveryOrderAddress = orderAddress;
                }
            }
        }

        return deliveryOrderAddress;
    }

    @Override
    public Order setDeliveryOrderAddress(OrderAddress deliveryOrderAddress) {
        deliveryOrderAddress.setAddressType(AddressType.DELIVERY);
        this.deliveryOrderAddress = deliveryOrderAddress;
        return this;
    }

    @Override
    public OrderAddress getInvoiceOrderAddress() {
        if (invoiceOrderAddress == null) {
            List<OrderAddress> orderAddressList = orderAddresses.thatBelongTo(this);
            for (OrderAddress orderAddress : orderAddressList) {
                if (orderAddress.getAddressType() == AddressType.INVOICE) {
                    invoiceOrderAddress = orderAddress;
                }
            }
        }

        return invoiceOrderAddress;
    }

    @Override
    public Order setInvoiceOrderAddress(OrderAddress invoiceOrderAddress) {
        invoiceOrderAddress.setAddressType(AddressType.INVOICE);
        this.invoiceOrderAddress = invoiceOrderAddress;
        return this;
    }

    @Override
    public List<OrderAddress> getOrderAddresses() {
        List<OrderAddress> addresses = new ArrayList<>();
        if (getInvoiceOrderAddress() != null)
            addresses.add(getInvoiceOrderAddress());
        if (getDeliveryOrderAddress() != null)
            addresses.add(getDeliveryOrderAddress());
        return addresses;
    }

    @Override
    public Order setOrderAddresses(List<OrderAddress> addresses) {
        for (OrderAddress address : addresses) {
            if (address.getAddressType() == AddressType.INVOICE)
                invoiceOrderAddress = address;
            else
                deliveryOrderAddress = address;
        }
        return this;
    }

    @Override
    public OrderPayment getOrderPayment() {
        if (orderPayment == null) {
            orderPayment = orderPayments.thatBelongTo(this);
        }

        return orderPayment;
    }

    @Override
    public Order setOrderPayment(OrderPayment orderPayment) {
        this.orderPayment = orderPayment;
        return this;
    }

    @Override
    public List<OrderShipment> getOrderShipments() {
        if (orderShipmentList == null) {
            List<OrderShipment> shipmentList = orderShipments.thatBelongTo(this);
            orderShipmentList = shipmentList;
            /*
             * if (shipmentList != null && shipmentList.size() > 0)
             * orderShipment = shipmentList.get(0);
             */

        }
        return orderShipmentList;
    }

    @Override
    public Order setOrderShipments(List<OrderShipment> orderShipmentList) {
        this.orderShipmentList = orderShipmentList;
        return this;
    }

    @Override
    public OrderShipment getOrderShipment() {
        if (getOrderShipments() != null && getOrderShipments().size() > 0)
            return getOrderShipments().get(0);
        return null;
    }

    @Override
    public Order addOrderShipment(OrderShipment orderShipment) {
        if (orderShipmentList == null)
            orderShipmentList = new ArrayList<>();
        orderShipmentList.add(orderShipment);
        return this;
    }

    @Override
    public Order setCouponCode(CouponCode couponCode) {
        this.couponCode = couponCode;
        if (couponCode == null)
            couponCodeId = null;
        else
            couponCodeId = couponCode.getId();
        return this;
    }

    @Override
    public CouponCode getCouponCode() {
        if (couponCode == null && couponCodeId != null) {
            CouponService couponService = app.service(CouponService.class);
            couponCode = couponService.getCouponCode(couponCodeId);
        }
        return couponCode;
    }

    @Override
    public Order setOperator(User operator) {
        if (operator == null) {
            this.operatorId = null;
            this.operator = null;
        } else {
            this.operatorId = operator.getId();
            this.operator = operator;
        }
        return this;
    }

    @Override
    public User getOperator() {
        if (operator == null && operatorId != null) {
            operator = userService.getUser(operatorId);
        }
        return operator;
    }

    @Override
    public Order setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
        return this;
    }

    @Override
    public String getOrderNumber() {
        return orderNumber;
    }

    @Override
    public CalculationResult getCalculationResult() {
        return calculationResult;
    }

    @Override
    public Order setCalculationResult(CalculationResult calculationResult) {
        this.calculationResult = calculationResult;
        if (calculationResult == null) {
            jsonCalculationResult = null;
        } else {
            jsonCalculationResult = Json.toJson(calculationResult);
        }
        return this;
    }

    @Override
    public Order setNote(String note) {
        this.note = note;
        return this;
    }

    @Override
    public String getNote() {
        return note;
    }

    @Override
    public Order setNoteInternal(String noteInternal) {
        this.noteInternal = noteInternal;
        return this;
    }

    @Override
    public String getNoteInternal() {
        return noteInternal;
    }

    @Override
    public Order setNoteSeller(String noteSeller) {
        this.noteSeller = noteSeller;
        return this;
    }

    @Override
    public String getNoteSeller() {
        return noteSeller;
    }

    @Override
    public Order setDiscountCode(String discountCode) {
        this.discountCode = discountCode;
        return this;
    }

    @Override
    public String getDiscountCode() {
        return discountCode;
    }

    @Override
    public Order setDiscountAmount(Double discountAmount) {
        this.discountAmount = discountAmount;
        return this;
    }

    @Override
    public Double getDiscountAmount() {
        return discountAmount;
    }

    @Override
    public Order setDiscountDescription(String discountDescription) {
        this.discountDescription = discountDescription;
        return this;
    }

    @Override
    public String getDiscountDescription() {
        return discountDescription;
    }

    @Override
    public Order setGiftDescription(String giftDescription) {
        this.giftDescription = giftDescription;
        return this;
    }

    @Override
    public String getGiftDescription() {
        return giftDescription;
    }

    @Override
    public boolean isCanceled() {
        return false;
    }

    @Override
    public Order setCanceled(boolean canceled) {
        return null;
    }

    @Override
    public String getLanguage() {
        return null;
    }

    @Override
    public Order setLanguage(String language) {
        return null;
    }

    @Override
    public List<OrderStatusHistory> getOrderStatusHistories() {
        if (orderStatusHistoryList == null) {
            orderStatusHistoryList = orderStatusHistories.thatBelongTo(this);
        }
        return orderStatusHistoryList;
    }

    @Override
    public Order setOrderStatusHistories(List<OrderStatusHistory> orderStatusHistories) {
        this.orderStatusHistoryList = orderStatusHistories;
        return this;
    }

    @Override
    public void fromMap(Map<String, Object> map) {
        super.fromMap(map);

        this.id = id_(map.get(Col.ID));
        this.id2 = str_(map.get(Col.ID2));
        this.requestContextId = id_(map.get(Col.REQUEST_CONTEXT_ID));
        this.checkoutId = id_(map.get(Col.CHECKOUT_ID));
        this.customerId = id_(map.get(Col.CUSTOMER_ID));
        this.totalAmount = double_(map.get(Col.TOTAL_AMOUNT));
        this.couponCodeId = id_(map.get(Col.COUPON_CODE_ID));

        this.jsonCalculationResult = str_(map.get(Col.CALCULATION_RESULT));
        if (jsonCalculationResult != null && !jsonCalculationResult.isEmpty()) {
            CalculationResult calculationResults = app.injectable(CalculationResult.class);
            try {
                this.calculationResult = calculationResults.fromJSON(jsonCalculationResult);
            } catch (JSONException e) {
                e.printStackTrace();
                throw new RuntimeException(e.getMessage(), e);
            }
        }
        this.orderNumber = str_(map.get(Col.ORDER_NUMBER));
        this.note = str_(map.get(Col.NOTE));
        this.noteInternal = str_(map.get(Col.NOTE_INTERNAL));
        this.noteSeller = str_(map.get(Col.NOTE_SELLER));
        this.orderStatus = enum_(OrderStatus.class, map.get(Col.ORDER_STATUS));
        this.discountCode = str_(map.get(Col.DISCOUNT_CODE));
        this.discountAmount = double_(map.get(Col.DISCOUNT_AMOUNT));
        this.discountDescription = str_(map.get(Col.DISCOUNT_DESCRIPTION));
        this.giftDescription = str_(map.get(Col.GIFT_DESCRIPTION));
        this.language = str_(map.get(Col.LANGUAGE));
        this.canceled = bool_(map.get(Col.CANCELED), false);
    }

    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> m = new LinkedHashMap<>(super.toMap());

        m.put(Col.ID, getId());
        m.put(Col.ID2, getId2());
        m.put(Col.REQUEST_CONTEXT_ID, getRequestContextId());
        m.put(Col.CHECKOUT_ID, getCheckoutId());
        m.put(Col.CUSTOMER_ID, getCustomerId());
        m.put(Col.TOTAL_AMOUNT, getTotalAmount());
        m.put(Col.COUPON_CODE_ID, couponCodeId);
        m.put(Col.CALCULATION_RESULT, jsonCalculationResult);
        m.put(Col.ORDER_NUMBER, orderNumber);
        m.put(Col.NOTE, getNote());
        m.put(Col.NOTE_INTERNAL, getNoteInternal());
        m.put(Col.NOTE_SELLER, getNoteSeller());
        if (getOrderStatus() != null) {
            m.put(Col.ORDER_STATUS, getOrderStatus().toId());
        }
        m.put(Col.DISCOUNT_CODE, getDiscountCode());
        m.put(Col.DISCOUNT_AMOUNT, getDiscountAmount());
        m.put(Col.DISCOUNT_DESCRIPTION, getDiscountDescription());
        m.put(Col.GIFT_DESCRIPTION, getGiftDescription());
        m.put(Col.LANGUAGE, getLanguage());
        m.put(Col.CANCELED, isCanceled());
        return m;
    }

    @JsonIgnore
    @Override
    public Checkout getCheckout() {
        if (this.checkout == null && checkoutId != null) {
            checkout = checkouts.findById(Checkout.class, checkoutId);
        }
        return checkout;
    }

    @Override
    public Customer getCustomer() {
        if (this.customer == null && customerId != null) {
            customer = customers.findById(Customer.class, customerId);
        }
        return customer;
    }
}
