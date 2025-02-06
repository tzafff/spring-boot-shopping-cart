package com.ctzaf.dreamshops.service.order;

import com.ctzaf.dreamshops.model.Order;

public interface IOrderService {
    Order placeOrder(Long userId);
    Order getOrder(Long orderId);
}
