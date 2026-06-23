package com.roy.ecommerce.controller;

import com.roy.ecommerce.dto.CartItemRequest;
import com.roy.ecommerce.dto.CartItemResponse;
import com.roy.ecommerce.model.CartItem;
import com.roy.ecommerce.service.CartService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cart")
public class CartController {
    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @PostMapping
    public CartItemResponse addToCart(@Valid @RequestBody CartItemRequest cartItemRequest,
                                      Authentication authentication) {
        String email = authentication.getName();
        return cartService.addToCart(email, cartItemRequest);
    }

    @GetMapping
    public List<CartItemResponse> getCartItems(Authentication authentication) {
        String email = authentication.getName();
        return cartService.getCartItems(email);
    }

    @DeleteMapping("/{cartItemId}")
    public String removeFromCart(@PathVariable Long cartItemId, Authentication authentication) {
        String email = authentication.getName();

        cartService.removeFromCart(cartItemId, email);

        return "Item removed from cart successfully";
    }
}
