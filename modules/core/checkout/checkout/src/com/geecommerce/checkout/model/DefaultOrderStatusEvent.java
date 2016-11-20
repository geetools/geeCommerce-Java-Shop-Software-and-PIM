package com.geecommerce.checkout.model;

import java.util.LinkedHashMap;
import java.util.Map;

import com.geecommerce.checkout.enums.OrderStatus;
import com.geecommerce.core.service.AbstractModel;
import com.geecommerce.core.service.annotation.Model;
import com.geecommerce.core.system.user.model.User;
import com.geecommerce.core.system.user.service.UserService;
import com.geecommerce.core.type.Id;
import com.google.inject.Inject;

@Model("sale_order_status_event")
public class DefaultOrderStatusEvent extends AbstractModel implements OrderStatusEvent {
    private static final long serialVersionUID = 5970073629771465130L;
    private Id id = null;
    private Id orderId = null;
    private OrderStatus orderStatus = null;
    private OrderStatus expectedOrderStatus = null;
    private boolean mailSent;
    private Id operatorId = null;
    private User operator = null;

    private final UserService userService;

    @Inject
    public DefaultOrderStatusEvent(UserService userService) {
        this.userService = userService;
    }

    @Override
    public Id getId() {
        return id;
    }

    @Override
    public OrderStatusEvent setId(Id id) {
        this.id = id;
        return this;
    }

    @Override
    public Id getOrderId() {
        return orderId;
    }

    @Override
    public OrderStatusEvent setOrderId(Id orderId) {
        this.orderId = orderId;
        return this;
    }

    @Override
    public OrderStatus getOrderStatus() {
        return orderStatus;
    }

    @Override
    public OrderStatusEvent setOrderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
        return this;
    }

    @Override
    public OrderStatusEvent belongsTo(Order order) {
        this.orderId = order.getId();
        return this;
    }

    @Override
    public OrderStatusEvent setOperator(User operator) {
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
    public boolean isMailSent() {
        return mailSent;
    }

    @Override
    public OrderStatusEvent setMailSent(boolean mailSent) {
        this.mailSent = mailSent;
        return this;
    }

    @Override
    public OrderStatus getExpectedOrderStatus() {
        return expectedOrderStatus;
    }

    @Override
    public OrderStatusEvent setExpectedOrderStatus(OrderStatus expectedOrderStatus) {
        this.expectedOrderStatus = expectedOrderStatus;
        return this;
    }

    @Override
    public void fromMap(Map<String, Object> map) {
        super.fromMap(map);

        this.id = id_(map.get(Column.ID));
        this.orderId = id_(map.get(Column.ORDER_ID));
        this.orderStatus = enum_(OrderStatus.class, map.get(Column.ORDER_STATUS));
        this.operatorId = id_(map.get(Column.OPERATOR));
        this.expectedOrderStatus = enum_(OrderStatus.class, map.get(Column.EXPECTED_ORDER_STATUS));
        this.mailSent = bool_(map.get(Column.MAIL_SENT));
    }

    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> m = new LinkedHashMap<>(super.toMap());

        m.put(Column.ID, getId());
        m.put(Column.ORDER_ID, getOrderId());
        m.put(Column.ORDER_STATUS, getOrderStatus() == null ? null : getOrderStatus().toId());
        m.put(Column.ORDER_STATUS, getExpectedOrderStatus() == null ? null : getExpectedOrderStatus().toId());
        m.put(Column.OPERATOR, operatorId);
        m.put(Column.MAIL_SENT, mailSent);

        return m;
    }
}
