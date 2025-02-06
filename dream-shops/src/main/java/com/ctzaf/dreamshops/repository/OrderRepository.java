package com.ctzaf.dreamshops.repository;

import com.ctzaf.dreamshops.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
