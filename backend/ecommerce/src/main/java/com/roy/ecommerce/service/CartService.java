package com.roy.ecommerce.service;

import com.roy.ecommerce.dto.CartItemRequest;
import com.roy.ecommerce.dto.CartItemResponse;
import com.roy.ecommerce.model.CartItem;
import com.roy.ecommerce.model.Product;
import com.roy.ecommerce.model.User;
import com.roy.ecommerce.repository.CartItemRepository;
import com.roy.ecommerce.repository.ProductRepository;
import com.roy.ecommerce.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CartService {

    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public CartService(CartItemRepository cartItemRepository, ProductRepository productRepository, UserRepository userRepository) {
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    public CartItemResponse addToCart(String email, CartItemRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        CartItem cartItem = cartItemRepository.findByUserAndProduct(user, product)
                .orElse(new CartItem());

        cartItem.setUser(user);
        cartItem.setProduct(product);
        cartItem.setQuantity(
                cartItem.getQuantity() == null
                ? request.getQuantity() :
                        cartItem.getQuantity() + request.getQuantity()
        );

        CartItem savedCartItem = cartItemRepository.save(cartItem);
        return mapToResponse(savedCartItem);
    }

    public List<CartItemResponse> getCartItems(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(()-> new RuntimeException("User Not Found"));

        return cartItemRepository.findByUser(user)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public void removeFromCart(Long cartItemId, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new RuntimeException("Cart item not found"));

        if(!cartItem.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("You cannot remove this cart item");
        }

        cartItemRepository.delete(cartItem);
    }

    private CartItemResponse mapToResponse(CartItem cartItem) {
        CartItemResponse cartItemResponse = new CartItemResponse();

        cartItemResponse.setCartItemId(cartItem.getId());
        cartItemResponse.setProductId(cartItem.getProduct().getId());
        cartItemResponse.setProductName(cartItem.getProduct().getName());
        cartItemResponse.setPrice(cartItem.getProduct().getPrice());
        cartItemResponse.setQuantity(cartItem.getQuantity());
        cartItemResponse.setTotalPrice(
                cartItem.getProduct().getPrice()* cartItem.getQuantity()
        );
        return cartItemResponse;
    }
}
