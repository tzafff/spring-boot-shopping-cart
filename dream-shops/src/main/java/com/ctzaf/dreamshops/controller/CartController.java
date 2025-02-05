package com.ctzaf.dreamshops.controller;

import com.ctzaf.dreamshops.exceptions.ResourceNotFoundException;
import com.ctzaf.dreamshops.model.Cart;
import com.ctzaf.dreamshops.response.ApiResponse;
import com.ctzaf.dreamshops.service.cart.ICartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@RequiredArgsConstructor
@RestController
@RequestMapping({"${api.prefix}/carts"})
public class CartController {

    private final ICartService cartService;

    /**
     * Gets the cart with the given id.
     *
     * @param cartId the id of the cart to be retrieved
     * @return the cart with the given id
     * @throws ResourceNotFoundException if no cart is found with the given id
     */
    @GetMapping("/{cartId}/my-cart")
    public ResponseEntity<ApiResponse> getCart(@PathVariable Long cartId) {
        try {
            Cart cart = cartService.getCart(cartId);
            return ResponseEntity.ok(new ApiResponse("Success", cart));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND).body(new ApiResponse(e.getMessage(), null));
        }
    }

    /**
     * Deletes all items from the cart with the given id.
     *
     * @param cartId the id of the cart to be cleared
     * @return a response with a success message if the cart is cleared successfully
     * @throws ResourceNotFoundException if no cart is found with the given id
     */
    @DeleteMapping("/{cartId}/clear")
    public ResponseEntity<ApiResponse> clearCart(@PathVariable Long cartId) {
        try {
            cartService.clearCart(cartId);
            return ResponseEntity.ok(new ApiResponse("Clear Cart Success!", null));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND).body(new ApiResponse(e.getMessage(), null));
        }
    }

/**
 * Retrieves the total price of all items in the cart with the given id.
 *
 * @param cartId the id of the cart to get the total price of
 * @return a response containing the total price of all items in the cart
 * @throws ResourceNotFoundException if no cart is found with the given id
 */
    @GetMapping("/{cartId}/cart/total-price")
    public ResponseEntity<ApiResponse> getTotalAmount(@PathVariable Long cartId) {
        try {
            BigDecimal totalPrice = cartService.getTotalPrice(cartId);
            return ResponseEntity.ok(new ApiResponse("Total Price", totalPrice));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND).body(new ApiResponse(e.getMessage(), null));
        }
    }
}
