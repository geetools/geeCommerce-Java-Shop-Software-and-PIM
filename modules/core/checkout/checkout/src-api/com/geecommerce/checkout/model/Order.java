package com.geecommerce.checkout.model;

import java.util.Date;
import java.util.List;

import com.geecommerce.calculation.model.CalculationResult;
import com.geecommerce.checkout.enums.OrderStatus;
import com.geecommerce.core.service.api.Model;
import com.geecommerce.core.system.model.RequestContext;
import com.geecommerce.core.system.user.model.User;
import com.geecommerce.core.type.Id;
import com.geecommerce.coupon.model.CouponCode;
import com.geecommerce.customer.model.Customer;

public interface Order extends Model {
    public Id getId();

    public Order setId(Id orderId);

    public String getId2();

    public Order setId2(String id2);

    public Id getRequestContextId();

    public Order setRequestContextId(Id requestContextId);

    public Id getCheckoutId();

    public Order setCheckoutId(Id checkoutId);

    public Order fromCheckout(Checkout checkout);

    public Id getCustomerId();

    public Order setCustomerId(Id customerId);

    public Double getTotalAmount();

    public Order setTotalAmount(Double totalAmount);

    public OrderStatus getOrderStatus();

    public Order setOrderStatus(OrderStatus orderStatus);

    public List<OrderItem> getOrderItems();

    public Order setOrderItems(List<OrderItem> orderItems);

    public Order addOrderItem(OrderItem orderItem);

    public Date getCreatedOn();

    public Date getModifiedOn();

    public OrderAddress getDeliveryOrderAddress();

    public Order setDeliveryOrderAddress(OrderAddress orderDeliveryAddress);

    public OrderAddress getInvoiceOrderAddress();

    public Order setInvoiceOrderAddress(OrderAddress orderInvoiceAddress);

    public List<OrderAddress> getOrderAddresses();

    public Order setOrderAddresses(List<OrderAddress> addresses);

    public OrderPayment getOrderPayment();

    public Order setOrderPayment(OrderPayment orderPayment);

    public Order belongsTo(Customer customer);

    public Order fromRequestContext(RequestContext requestContext);

    public List<OrderShipment> getOrderShipments();

    public Order setOrderShipments(List<OrderShipment> orderShipments);

    public OrderShipment getOrderShipment();

    public Order addOrderShipment(OrderShipment orderShipment);

    public Checkout getCheckout();

    Customer getCustomer();

    public Order setCouponCode(CouponCode couponCode);

    public CouponCode getCouponCode();

    public Order setOperator(User operator);

    public User getOperator();

    public Order setOrderNumber(String orderNumber);

    public String getOrderNumber();

    public CalculationResult getCalculationResult();

    public Order setCalculationResult(CalculationResult calculationResult);

    public Order setNote(String note);

    public String getNote();

    public Order setNoteInternal(String noteInternal);

    public String getNoteInternal();

    public Order setNoteSeller(String noteSeller);

    public String getNoteSeller();

    public Order setDiscountCode(String discountCode);

    public String getDiscountCode();

    public Order setDiscountAmount(Double discountAmount);

    public Double getDiscountAmount();

    public Order setDiscountDescription(String discountDescription);

    public String getDiscountDescription();

    public Order setGiftDescription(String giftDescription);

    public String getGiftDescription();

    public boolean isCanceled();

    public Order setCanceled(boolean canceled);

    public String getLanguage();

    public Order setLanguage(String language);

    public List<OrderStatusHistory> getOrderStatusHistories();

    public Order setOrderStatusHistories(List<OrderStatusHistory> orderStatusHistories);

    static final class Col {
        public static final String ID = "_id";
        public static final String ID2 = "id2";
        public static final String REQUEST_CONTEXT_ID = "req_ctx_id";
        public static final String CHECKOUT_ID = "checkout_id";
        public static final String CUSTOMER_ID = "customer_fk";
        public static final String TOTAL_AMOUNT = "total_amount";
        public static final String COUPON_CODE_ID = "coupon_id";
        public static final String OPERATOR = "operator";
        public static final String ORDER_NUMBER = "order_number";
        public static final String CALCULATION_RESULT = "calc_result";
        public static final String NOTE = "note";
        public static final String NOTE_INTERNAL = "note_internal";
        public static final String NOTE_SELLER = "note_seller";
        public static final String ORDER_STATUS = "order_status";
        public static final String DISCOUNT_CODE = "discount_code";
        public static final String DISCOUNT_AMOUNT = "discount_amount";
        public static final String DISCOUNT_DESCRIPTION = "discount_description";
        public static final String GIFT_DESCRIPTION = "gift_description";
        public static final String CANCELED = "canceled";
        public static final String LANGUAGE = "lang";
    }
}
