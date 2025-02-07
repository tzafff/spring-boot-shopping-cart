package com.ctzaf.dreamshops.controller;


import com.ctzaf.dreamshops.exceptions.ResourceNotFoundException;
import com.ctzaf.dreamshops.model.Cart;
import com.ctzaf.dreamshops.model.User;
import com.ctzaf.dreamshops.response.ApiResponse;
import com.ctzaf.dreamshops.service.cart.ICartItemService;
import com.ctzaf.dreamshops.service.cart.ICartService;
import com.ctzaf.dreamshops.service.user.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@RequiredArgsConstructor
@RestController
@RequestMapping({"${api.prefix}/cartItems"})
public class CartItemController {

    private final ICartItemService cartItemService;
    private final ICartService cartService;
    private final IUserService userService;

/**
 * Adds an item to the cart. If the cart does not exist, a new cart is initialized.
 *
 * @param productId the id of the product to be added to the cart
 * @param quantity the quantity of the product to be added
 * @return a ResponseEntity with a status of 200 and a body of ApiResponse containing the message "Add Item Success"
 * if the addition was successful, otherwise a ResponseEntity with a status of 404 and a body of ApiResponse containing
 * the message from ResourceNotFoundException if the product could not be found
 */
    @PostMapping("/item/add")
    public ResponseEntity<ApiResponse> addItemToCart(
                                                     @RequestParam Long productId,
                                                     @RequestParam Integer quantity) {
        try {
            User user = userService.getUserById(4L);
            Cart cart = cartService.initializeNewCart(user);

            cartItemService.addItemToCart(cart.getId(), productId, quantity);
            return ResponseEntity.ok(new ApiResponse("Add Item Success", null));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND).body(new ApiResponse(e.getMessage(), null));
        }
    }

/**
 * Removes a specific item from the cart.
 *
 * @param cartId the id of the cart from which the item is to be removed
 * @param itemId the id of the item to be removed
 * @return a ResponseEntity with a status of 200 and a body of ApiResponse containing
 * the message "Remove Item Success" if the removal was successful, otherwise a
 * ResponseEntity with a status of 404 and a body of ApiResponse containing the
 * message from ResourceNotFoundException if the item could not be found
 */
    @DeleteMapping("/cart/{cartId}/item/{itemId}/remove")
    public ResponseEntity<ApiResponse> removeItemFromCart(@PathVariable Long cartId,
                                                          @PathVariable Long itemId) {
        try {
            cartItemService.removeItemFromCart(cartId, itemId);
            return ResponseEntity.ok(new ApiResponse("Remove Item Success", null));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND).body(new ApiResponse(e.getMessage(), null));
        }
    }

    /**
     * Updates the quantity of a specific item in the cart.
     *
     * @param cartId the id of the cart to update the item in
     * @param itemId the id of the item to update the quantity of
     * @param quantity the new quantity of the item
     * @return a ResponseEntity with a status of 200 and a body of ApiResponse containing the message "Update Quantity Success"
     * if the update was successful, otherwise a ResponseEntity with a status of 404 and a body of ApiResponse containing the
     * message "Item Not Found!" if the item could not be found in the cart
     */
    @PutMapping("/cart/{cartId}/item/{itemId}/update")
    public ResponseEntity<ApiResponse> updateItemQuantity(@PathVariable Long cartId,
                                                          @PathVariable Long itemId,
                                                          @RequestParam Integer quantity) {
        try {
            cartItemService.updateQuantity(cartId, itemId, quantity);
            return ResponseEntity.ok(new ApiResponse("Update Quantity Success", null));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND).body(new ApiResponse(e.getMessage(), null));
        }
    }

}
