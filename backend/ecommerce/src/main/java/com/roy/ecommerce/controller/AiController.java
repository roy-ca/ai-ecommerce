package com.roy.ecommerce.controller;

import com.roy.ecommerce.dto.ProductDescriptionRequest;
import com.roy.ecommerce.service.AiProductService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ai")
public class AiController {

    private final AiProductService aiProductService;

    public AiController(AiProductService aiProductService) {
        this.aiProductService = aiProductService;
    }

    @PostMapping("/product-description")
    public String generateDescription(@RequestBody ProductDescriptionRequest request) {
        return aiProductService.generateProductDescription(request);
    }
}
