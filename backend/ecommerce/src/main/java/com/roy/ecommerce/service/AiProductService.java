package com.roy.ecommerce.service;

import com.roy.ecommerce.dto.ProductDescriptionRequest;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
public class AiProductService {
    private final ChatClient chatClient;

    public AiProductService(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    public String generateProductDescription(ProductDescriptionRequest request) {

        String prompt = """
                 Generate a professional e-commerce product description.
                
                                Product Name: %s
                                Category: %s
                                Features: %s
                
                                Keep it concise, attractive, and suitable for an online product listing.
                """.formatted(
                        request.getProductName(),
                request.getCategory(),
                request.getFeatures()
        );

        return chatClient
                .prompt(prompt)
                .call()
                .content();
    }
}
