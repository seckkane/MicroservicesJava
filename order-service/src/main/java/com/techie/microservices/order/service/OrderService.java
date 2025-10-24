package com.techie.microservices.order.service;

import com.techie.microservices.order.client.InventoryClient;
import com.techie.microservices.order.dto.OrderRequest;
import com.techie.microservices.order.model.Order;
import com.techie.microservices.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final InventoryClient inventoryClient;

    public void placeOrder(OrderRequest orderRequest) {

        var isProductInStock = inventoryClient.isInstock(orderRequest.skuCode(), orderRequest.quantity());
        if (isProductInStock) {
            // Convertir le DTO en entité Order
            Order order = new Order();
            order.setOrderNumber(UUID.randomUUID().toString());
            order.setPrice(orderRequest.price());
            order.setSkuCode(orderRequest.skuCode());
            order.setQuantity(orderRequest.quantity());
            // order.setUserId(orderRequest.userDetails().getUserId()); // ou autre mapping selon UserDetails

            // Sauvegarder dans la base
            orderRepository.save(order);
        }
        else {
            //throw new RuntimeException("Product with SkuCode " + orderRequest.skuCode() + " is not in stock");
            throw new ProductOutOfStockException(orderRequest.skuCode(), orderRequest.quantity());
            //log.warn("Produit '{}' indisponible pour cette quantité : {}", orderRequest.skuCode(), orderRequest.quantity());
        }


    }
}
