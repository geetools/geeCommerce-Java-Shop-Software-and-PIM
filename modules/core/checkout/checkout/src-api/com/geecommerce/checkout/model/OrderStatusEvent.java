package com.geecommerce.checkout.model;

import com.geecommerce.checkout.enums.OrderStatus;
import com.geecommerce.core.service.api.Model;
import com.geecommerce.core.system.user.model.User;
import com.geecommerce.core.type.Id;

public interface OrderStatusEvent extends Model {
    public Id getId();

    public OrderStatusEvent setId(Id id);

    public Id getOrderId();

    public OrderStatusEvent setOrderId(Id orderId);

    public OrderStatus getOrderStatus();

    public OrderStatusEvent setOrderStatus(OrderStatus orderStatus);

    public OrderStatusEvent setOperator(User operator);

    public User getOperator();

    public OrderStatusEvent belongsTo(Order order);

    boolean isMailSent();

    OrderStatusEvent setMailSent(boolean mailSent);

    OrderStatus getExpectedOrderStatus();

    OrderStatusEvent setExpectedOrderStatus(OrderStatus expectedOrderStatus);

    static final class Column {
        public static final String ID = "_id";
        public static final String ORDER_ID = "order_fk";
        public static final String ORDER_STATUS = "order_status";
        public static final String OPERATOR = "operator";
        public static final String EXPECTED_ORDER_STATUS = "expected_order_status";
        public static final String MAIL_SENT = "mail_sent";
    }
}
