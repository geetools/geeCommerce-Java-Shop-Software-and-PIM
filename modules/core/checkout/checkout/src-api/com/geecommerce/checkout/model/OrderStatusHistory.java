package com.geecommerce.checkout.model;

import com.geecommerce.checkout.enums.OrderStatus;
import com.geecommerce.core.service.api.Model;
import com.geecommerce.core.system.user.model.User;
import com.geecommerce.core.type.Id;

public interface OrderStatusHistory extends Model {
    public Id getId();

    public OrderStatusHistory setId(Id id);

    public Id getOrderId();

    public OrderStatusHistory setOrderId(Id orderId);

    public OrderStatus getOrderStatus();

    public OrderStatusHistory setOrderStatus(OrderStatus orderStatus);

    public OrderStatusHistory setOperator(User operator);

    public User getOperator();

    public OrderStatusHistory belongsTo(Order order);

    static final class Column {
        public static final String ID = "_id";
        public static final String ORDER_ID = "order_fk";
        public static final String ORDER_STATUS = "order_status";
        public static final String OPERATOR = "operator";
    }
}
