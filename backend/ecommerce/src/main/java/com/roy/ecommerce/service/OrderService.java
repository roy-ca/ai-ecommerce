package com.roy.ecommerce.service;

import com.roy.ecommerce.dto.OrderItemResponse;
import com.roy.ecommerce.dto.OrderResponse;
import com.roy.ecommerce.dto.OrderStatusUpdateRequest;
import com.roy.ecommerce.model.*;
import com.roy.ecommerce.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class OrderService {

    private final UserRepository userRepository;
    private final CartItemRepository cartItemRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductRepository productRepository;

    public OrderService(UserRepository userRepository, CartItemRepository cartItemRepository, OrderRepository orderRepository, OrderItemRepository orderItemRepository, ProductRepository productRepository) {
        this.userRepository = userRepository;
        this.cartItemRepository = cartItemRepository;
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.productRepository = productRepository;
    }

    @Transactional
    public OrderResponse placeOrder(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<CartItem> cartItems = cartItemRepository.findByUser(user);
        if(cartItems.isEmpty()) {
            throw new RuntimeException("Cart is empty");
        }

        for(CartItem cartItem : cartItems) {

            Product product = cartItem.getProduct();

            if(product.getStockQuantity() < cartItem.getQuantity()) {
                throw new RuntimeException(product.getName() + " is out of stock");
            }
        }

        Double totalAmount = cartItems.stream()
                .mapToDouble(cartItem ->
                        cartItem.getProduct().getPrice() * cartItem.getQuantity()
                        )
                .sum();

        Order order = new Order();
        order.setUser(user);
        order.setTotalAmount(totalAmount);
        order.setStatus(OrderStatus.PENDING);

        Order savedOrder = orderRepository.save(order);

        List<OrderItem> orderItems = cartItems.stream()
                .map(cartItem -> {
                    OrderItem orderItem = new OrderItem();

                    orderItem.setOrder(savedOrder);
                    orderItem.setProduct(cartItem.getProduct());
                    orderItem.setQuantity(cartItem.getQuantity());
                    orderItem.setPrice(cartItem.getProduct().getPrice());

                    return orderItem;
                })
                .toList();
        orderItemRepository.saveAll(orderItems);

        for(CartItem cartItem : cartItems) {
            Product product = cartItem.getProduct();

            product.setStockQuantity(product.getStockQuantity() - cartItem.getQuantity());
            productRepository.save(product);
        }

        cartItemRepository.deleteAll(cartItems);
        return mapToResponse(savedOrder, orderItems);
    }

    public OrderResponse updateOrderStatus(Long orderId, OrderStatusUpdateRequest request) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        order.setStatus(request.getStatus());

        Order savedOrder = orderRepository.save(order);

        List<OrderItem> orderItems = orderItemRepository.findByOrder(savedOrder);

        return mapToResponse(savedOrder, orderItems);
    }

    private OrderResponse mapToResponse(Order order, List<OrderItem> orderItems) {
        OrderResponse response = new OrderResponse();

        response.setOrderId(order.getId());
        response.setTotalAmount(order.getTotalAmount());
        response.setStatus(order.getStatus());

        List<OrderItemResponse> itemResponses = orderItems.stream()
                .map(orderItem -> {
                    OrderItemResponse itemResponse = new OrderItemResponse();

                    itemResponse.setProductId(orderItem.getProduct().getId());
                    itemResponse.setProductName(orderItem.getProduct().getName());
                    itemResponse.setQuantity(orderItem.getQuantity());
                    itemResponse.setPrice(orderItem.getPrice());
                    itemResponse.setTotalPrice(
                            orderItem.getPrice() * orderItem.getQuantity()
                    );
                    return itemResponse;
                })
                .toList();
        response.setItems(itemResponses);
        return response;
    }

    public List<OrderResponse> getOrders(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User Not Found"));

        return orderRepository.findByUser(user)
                .stream()
                .map(order -> {
                    List<OrderItem> orderItems = orderItemRepository.findByOrder(order);

                    return mapToResponse(order, orderItems);
                })
                .toList();
    }
}
