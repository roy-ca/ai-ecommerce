package com.roy.ecommerce.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductDescriptionRequest {

    private String productName;
    private String category;
    private String features;
}
