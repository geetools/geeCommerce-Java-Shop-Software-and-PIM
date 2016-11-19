package com.geecommerce.checkout.repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.geecommerce.checkout.enums.OrderStatus;
import com.geecommerce.checkout.model.Order;
import com.geecommerce.checkout.model.OrderStatusHistory;
import com.geecommerce.core.service.AbstractRepository;
import com.geecommerce.core.service.QueryOptions;
import com.geecommerce.core.service.annotation.Repository;
import com.geecommerce.core.service.api.GlobalColumn;

@Repository
public class DefaultOrderStatusHistories extends AbstractRepository implements OrderStatusHistories {
    @Override
    public List<OrderStatusHistory> thatBelongTo(Order order) {
        Map<String, Object> filter = new HashMap<>();
        filter.put(OrderStatusHistory.Column.ORDER_ID, order.getId());
        List<OrderStatusHistory> orderStatusHistories = find(OrderStatusHistory.class, filter, QueryOptions.builder().sortBy(GlobalColumn.CREATED_ON).build());
        return orderStatusHistories;
    }

    @Override
    public OrderStatusHistory getHistoryForStatus(Order order, OrderStatus orderStatus) {
        Map<String, Object> filter = new HashMap<>();
        filter.put(OrderStatusHistory.Column.ORDER_ID, order.getId());
        filter.put(OrderStatusHistory.Column.ORDER_STATUS, orderStatus.toId());
        List<OrderStatusHistory> orderStatusHistories = find(OrderStatusHistory.class, filter, QueryOptions.builder().sortBy(GlobalColumn.CREATED_ON).build());
        if (orderStatusHistories != null && !orderStatusHistories.isEmpty()) {
            return orderStatusHistories.get(0);
        }
        return null;
    }

    @Override
    public OrderStatusHistory getLastStatusHistory(Order order) {
        Map<String, Object> filter = new HashMap<>();
        filter.put(OrderStatusHistory.Column.ORDER_ID, order.getId());
        List<OrderStatusHistory> orderStatusHistories = find(OrderStatusHistory.class, filter, QueryOptions.builder().sortByDesc(GlobalColumn.MODIFIED_ON).build());
        if (orderStatusHistories != null && !orderStatusHistories.isEmpty()) {
            return orderStatusHistories.get(0);
        }
        return null;
    }
}
