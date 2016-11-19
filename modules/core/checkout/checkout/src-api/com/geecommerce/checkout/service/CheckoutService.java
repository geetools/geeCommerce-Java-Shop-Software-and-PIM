package com.geecommerce.checkout.service;

import java.util.List;
import java.util.Map;

import com.geecommerce.checkout.model.Checkout;
import com.geecommerce.checkout.model.Order;
import com.geecommerce.checkout.model.OrderPayment;
import com.geecommerce.core.service.QueryOptions;
import com.geecommerce.core.service.api.Service;
import com.geecommerce.core.type.Id;

public interface CheckoutService extends Service {
    public Checkout getCheckout(Id cartId);

    public Checkout createCheckout(Checkout checkout);

    public void updateCheckout(Checkout checkout);

    public Order createOrder(Order order);

    public void removeOrder(Order order);

    public List<Order> getOrders();

    public List<Order> getOrders(Map<String, Object> query, QueryOptions queryOptions);

    public Order getOrder(Id orderId);

    public Order getOrderByTransaction(String transactionId);

    public Order getOrderByNumber(String orderNumber);

    public void updateOrderPayment(OrderPayment orderPayment);

    public List<Order> getOrdersByCustomerId(Id customerId);

}
