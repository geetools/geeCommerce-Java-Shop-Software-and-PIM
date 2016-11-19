package com.geecommerce.checkout.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.geecommerce.calculation.helper.CalculationHelper;
import com.geecommerce.calculation.service.CalculationService;
import com.geecommerce.checkout.model.Checkout;
import com.geecommerce.checkout.model.Order;
import com.geecommerce.checkout.model.OrderAddress;
import com.geecommerce.checkout.model.OrderItem;
import com.geecommerce.checkout.model.OrderPayment;
import com.geecommerce.checkout.model.OrderPaymentEvent;
import com.geecommerce.checkout.model.OrderShipment;
import com.geecommerce.checkout.model.OrderShipmentItem;
import com.geecommerce.checkout.repository.Checkouts;
import com.geecommerce.checkout.repository.OrderAddresses;
import com.geecommerce.checkout.repository.OrderItems;
import com.geecommerce.checkout.repository.OrderPaymentEvents;
import com.geecommerce.checkout.repository.OrderPayments;
import com.geecommerce.checkout.repository.OrderShipmentItems;
import com.geecommerce.checkout.repository.OrderShipments;
import com.geecommerce.checkout.repository.Orders;
import com.geecommerce.core.App;
import com.geecommerce.core.service.QueryOptions;
import com.geecommerce.core.service.annotation.Service;
import com.geecommerce.core.service.annotation.Transactional;
import com.geecommerce.core.type.Id;
import com.geecommerce.inventory.repository.Stocks;
import com.geecommerce.shipping.service.ShippingService;
import com.google.inject.Inject;

@Service
public class DefaultCheckoutService implements CheckoutService {
    @Inject
    protected App app;

    protected final Checkouts checkouts;
    protected final Orders orders;
    protected final OrderItems orderItems;
    protected final OrderAddresses orderAddresses;
    protected final OrderPayments orderPayments;
    protected final OrderPaymentEvents orderPaymentEvents;
    protected final Stocks stocks;
    protected final ShippingService shippingService;
    protected final CalculationService calculationService;
    protected final CalculationHelper calculationHelper;
    protected final OrderShipments orderShipments;
    protected final OrderShipmentItems orderShipmentItems;

    @Inject
    public DefaultCheckoutService(Checkouts checkouts, Orders orders, OrderItems orderItems, OrderAddresses orderAddresses, OrderPayments orderPayments, OrderPaymentEvents orderPaymentEvents,
        OrderShipments orderShipments,
        OrderShipmentItems orderShipmentItems, Stocks stocks, ShippingService shippingService, CalculationService calculationService, CalculationHelper calculationHelper) {
        this.checkouts = checkouts;
        this.orders = orders;
        this.orderItems = orderItems;
        this.orderAddresses = orderAddresses;
        this.orderPayments = orderPayments;
        this.orderPaymentEvents = orderPaymentEvents;
        this.orderShipments = orderShipments;
        this.orderShipmentItems = orderShipmentItems;
        this.stocks = stocks;
        this.shippingService = shippingService;
        this.calculationService = calculationService;
        this.calculationHelper = calculationHelper;
    }

    @Override
    public Checkout getCheckout(Id checkoutId) {
        return checkouts.findById(Checkout.class, checkoutId);
    }

    @Override
    public Checkout createCheckout(Checkout checkout) {
        return checkouts.add(checkout);
    }

    @Override
    public void updateCheckout(Checkout checkout) {
        checkouts.update(checkout);
    }

    @Override
    @Transactional
    public Order createOrder(Order order) {
        if (order == null)
            return null;

        boolean update = order.getId() == null ? false : true;

        // Save order
        Order savedOrder = order;
        if (update) {
            orders.update(order);
        } else {
            savedOrder = orders.add(order);
        }

        // Save order items
        List<OrderItem> orderItemsList = order.getOrderItems();

        if (orderItemsList != null && orderItemsList.size() > 0) {
            List<OrderItem> savedOrderItems = new ArrayList<>();

            for (OrderItem orderItem : orderItemsList) {
                // Make sure that the order item has the saved orderId
                orderItem.belongsTo(savedOrder).setId(app.nextId());

                OrderItem savedOrderItem = orderItems.add(orderItem);
                savedOrderItems.add(savedOrderItem);

                // decrement quantity from inventory stock
                // stocks.decrementQty(orderItem.getProductId(),
                // app.getApplicationContext().getRequestContext(),
                // orderItem.getQuantity());
            }

            savedOrder.setOrderItems(savedOrderItems);
        }

        // Order address
        List<OrderAddress> addresses = order.getOrderAddresses();
        List<OrderAddress> savedAddresses = new ArrayList<>();
        for (OrderAddress address : addresses) {
            address.belongsTo(savedOrder).setId(app.nextId());
            OrderAddress savedAddress = orderAddresses.add(address);
            savedAddresses.add(savedAddress);
        }
        savedOrder.setOrderAddresses(savedAddresses);

        // Order payment
        OrderPayment payment = order.getOrderPayment();

        if (payment != null) {
            payment.belongsTo(savedOrder).setId(app.nextId());

            OrderPayment savedPayment = orderPayments.add(payment);
            savedOrder.setOrderPayment(savedPayment);
        }

        List<OrderShipment> savedOrderShipments = new ArrayList<>();
        for (OrderShipment shipment : order.getOrderShipments()) {
            if (shipment != null) {
                shipment.belongsTo(savedOrder).setId(app.nextId());
                OrderShipment savedShipment = this.orderShipments.add(shipment);

                List<OrderShipmentItem> savedOrderShipmentItems = new ArrayList<>();
                for (OrderItem orderItem : savedOrder.getOrderItems()) {

                    // TODO: fix this crazy stuff
                    OrderShipmentItem orderShipmentItem = app.getModel(OrderShipmentItem.class);
                    orderShipmentItem.setId(app.nextId());
                    orderShipmentItem.setOrderItemId(orderItem.getId());
                    orderShipmentItem.belongsTo(savedOrder.getOrderShipment());

                    OrderShipmentItem savedOrderShipmentItem = orderShipmentItems.add(orderShipmentItem);
                    savedOrderShipmentItems.add(savedOrderShipmentItem);
                }

                savedShipment.setShipmentItems(savedOrderShipmentItems);
                savedOrderShipments.add(savedShipment);
            }
        }

        savedOrder.setOrderShipments(savedOrderShipments);

        return savedOrder;
    }

    @Override
    @Transactional
    public void removeOrder(Order order) {
        if (order == null)
            return;

        List<OrderItem> orderItemsList = order.getOrderItems();

        if (orderItemsList != null) {
            for (OrderItem orderItem : orderItemsList) {
                orderItems.remove(orderItem);
            }
        }

        List<OrderAddress> addresses = order.getOrderAddresses();
        if (addresses != null) {
            for (OrderAddress address : addresses) {
                orderAddresses.remove(address);
            }
        }

        OrderPayment payment = order.getOrderPayment();
        if (payment != null) {
            List<OrderPaymentEvent> ope = payment.getPaymentEvents();
            if (ope != null) {
                for (OrderPaymentEvent orderPaymentEvent : ope) {
                    orderPaymentEvents.remove(orderPaymentEvent);
                }
            }

            orderPayments.remove(payment);
        }

        for (OrderShipment shipment : order.getOrderShipments()) {
            if (shipment != null) {
                List<OrderShipmentItem> orderShipmentItems = shipment.getShipmentItems();
                if (orderShipmentItems != null) {
                    for (OrderShipmentItem shipmentItem : orderShipmentItems) {
                        orderShipmentItems.remove(shipmentItem);
                    }
                }

                orderShipments.remove(shipment);
            }
        }

        orders.remove(order);
    }

    @Override
    public List<Order> getOrders() {
        return orders.findAll(Order.class);
    }

    @Override
    public List<Order> getOrders(Map<String, Object> query, QueryOptions queryOptions) {
        return orders.find(Order.class, query, queryOptions);
    }

    @Override
    public Order getOrder(Id orderId) {
        if (orderId == null)
            return null;

        Order order = orders.findById(Order.class, orderId);
        return order;
    }

    @Override
    public Order getOrderByTransaction(String transactionId) {

        Map<String, Object> filter = new HashMap<>();
        filter.put(OrderPayment.Column.TRANSACTION_ID, transactionId);
        OrderPayment orderPayment = orderPayments.findOne(OrderPayment.class, filter);

        Order order = orders.findById(Order.class, orderPayment.getOrderId());
        return order;
    }

    @Override
    public Order getOrderByNumber(String orderNumber) {
        Map<String, Object> filter = new HashMap<>();
        filter.put(Order.Col.ORDER_NUMBER, orderNumber);

        List<Order> orderList = orders.find(Order.class, filter);
        if (orderList == null || orderList.size() == 0)
            return null;

        Order order = orderList.stream().max((o1, o2) -> o1.getCreatedOn().compareTo(o2.getCreatedOn())).get();

        return order;
    }

    @Override
    public void updateOrderPayment(OrderPayment orderPayment) {
        orderPayments.update(orderPayment);
    }

    @Override
    public List<Order> getOrdersByCustomerId(Id customerId) {
        Map<String, Object> filter = new HashMap<>();
        filter.put(Order.Col.CUSTOMER_ID, customerId);

        List<Order> all = orders.find(Order.class, filter);
        return all;
    }

}