package com.ctzaf.dreamshops.service.order;

import com.ctzaf.dreamshops.enums.OrderStatus;
import com.ctzaf.dreamshops.exceptions.ResourceNotFoundException;
import com.ctzaf.dreamshops.model.Cart;
import com.ctzaf.dreamshops.model.Order;
import com.ctzaf.dreamshops.model.OrderItem;
import com.ctzaf.dreamshops.model.Product;
import com.ctzaf.dreamshops.repository.OrderRepository;
import com.ctzaf.dreamshops.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService implements IOrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    @Override
    public Order placeOrder(Long userId) {
        return null;
    }

    @Override
    public Order getOrder(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found!"));
    }

    private Order createOrder(Cart cart) {
        Order order = new Order();
        // TODO set the user
        order.setOrderStatus(OrderStatus.PENDING);
        order.setOrderDate(LocalDate.now());
        return order;
    }

    private List<OrderItem> createOrderItems(Order order, Cart cart) {
        return cart.getItems().stream().map(cartItem -> {
            Product product = cartItem.getProduct();
            product.setInventory(product.getInventory() - cartItem.getQuantity());
            productRepository.save(product);
            return new OrderItem(
                    order,
                    product,
                    cartItem.getQuantity(),
                    cartItem.getUnitPrice()
            );
        }).toList();
    }

    private BigDecimal calculateTotalAmount(List<OrderItem> orderItems) {
        return orderItems
                .stream()
                .map(item -> item.getPrice()
                        .multiply(new BigDecimal(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
