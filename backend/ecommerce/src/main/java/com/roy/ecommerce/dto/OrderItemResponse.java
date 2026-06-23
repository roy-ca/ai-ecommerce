package com.roy.ecommerce.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class OrderItemResponse {

    private Long productId;
    private String productName;
    private Integer quantity;
    private Double price;
    private Double totalPrice;
}
