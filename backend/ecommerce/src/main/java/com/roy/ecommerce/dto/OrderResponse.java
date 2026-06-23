package com.roy.ecommerce.dto;
import com.roy.ecommerce.model.OrderStatus;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class OrderResponse {
    private Long orderId;
    private Double totalAmount;
    private OrderStatus status;
    private List<OrderItemResponse> items;
}
