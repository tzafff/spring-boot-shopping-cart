package com.ctzaf.dreamshops.service.order;

import com.ctzaf.dreamshops.dto.OrderDto;
import com.ctzaf.dreamshops.model.Order;

import java.util.List;

public interface IOrderService {
    Order placeOrder(Long userId);
    OrderDto getOrder(Long orderId);

    List<OrderDto> getUserOrders(Long userId);

    OrderDto convertToDto(Order order);
}
