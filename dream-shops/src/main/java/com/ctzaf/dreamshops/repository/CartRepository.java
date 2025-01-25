package com.ctzaf.dreamshops.repository;

import com.ctzaf.dreamshops.model.Cart;
import org.springframework.data.jpa.repository.JpaRepository;


public interface CartRepository extends JpaRepository<Cart, Long> {

}
