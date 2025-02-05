package com.ctzaf.dreamshops.service.cart;

import com.ctzaf.dreamshops.exceptions.ResourceNotFoundException;
import com.ctzaf.dreamshops.model.Cart;
import com.ctzaf.dreamshops.repository.CartItemRepository;
import com.ctzaf.dreamshops.repository.CartRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.concurrent.atomic.AtomicLong;

@Service
@RequiredArgsConstructor
public class CartService implements ICartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final AtomicLong cartIdGenerator = new AtomicLong(0);

    /**
     * Retrieves a cart by id.
     *
     * @param id the id of the cart to be retrieved
     * @return the cart with the given id
     * @throws ResourceNotFoundException if no cart is found with the given id
     */
    @Override
    public Cart getCart(Long id) {
        Cart cart  = cartRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found"));
        BigDecimal totalAmount = cart.getTotalAmount();
        cart.setTotalAmount(totalAmount);
        return cartRepository.save(cart);
    }

    /**
     * Deletes all items from the cart with the given id and then deletes the cart.
     *
     * @param id the id of the cart to be cleared
     * @throws ResourceNotFoundException if no cart is found with the given id
     */
    @Transactional
    @Override
    public void clearCart(Long id) {
        Cart cart = getCart(id);
        cartItemRepository.deleteAllByCartId(id);
        cart.getItems().clear();
        cartRepository.deleteById(id);
    }

    /**
     * Retrieves the total price of all items in the cart with the given id.
     *
     * @param id the id of the cart to get the total price of
     * @return the total price of all items in the cart
     * @throws ResourceNotFoundException if no cart is found with the given id
     */
    @Override
    public BigDecimal getTotalPrice(Long id) {
        Cart cart = getCart(id);
        return cart.getTotalAmount();
    }

    @Override
    public Long initializeNewCart() {
        Cart newCart = new Cart();
        Long newCartId = cartIdGenerator.incrementAndGet();
        newCart.setId(newCartId);
        return cartRepository.save(newCart).getId();
    }
}
