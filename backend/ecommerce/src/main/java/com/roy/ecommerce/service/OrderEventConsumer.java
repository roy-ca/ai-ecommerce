package com.roy.ecommerce.service;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class OrderEventConsumer {

    @KafkaListener(topics = "order-created", groupId = "ecommerce-group")
    public void consumeOrderCreatedEvent(String orderId) {
        System.out.println("Order Created Event Received. Order Id: "+ orderId);
    }
}
