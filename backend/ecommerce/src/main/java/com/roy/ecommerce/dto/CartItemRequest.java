package com.roy.ecommerce.dto;


import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CartItemRequest {

    @NotNull
    private Long productId;

    @NotNull
    private Integer quantity;
}
