package com.techie.microservices.order.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.techie.microservices.order.client.InventoryClient;
import com.techie.microservices.order.dto.ApiResponse;
import com.techie.microservices.order.dto.OrderRequest;
import com.techie.microservices.order.exception.OrderException;
import com.techie.microservices.order.model.Order;
import com.techie.microservices.order.repository.OrderRepository;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final InventoryClient inventoryClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Transactional
    public void placeOrder(OrderRequest orderRequest) {

        // 1️⃣ Log avant de vérifier l’inventaire
        log.info("Checking inventory for product {} with quantity {}", orderRequest.skuCode(), orderRequest.quantity());

        // 2️⃣ Vérifier l’inventaire via InventoryClient
        try {
            inventoryClient.isInstock(orderRequest.skuCode(), orderRequest.quantity());

        } catch (FeignException.BadRequest ex) {
            // Désérialiser proprement la réponse JSON d’Inventory
            ApiResponse<String> inventoryResponse;
            try {
                inventoryResponse = objectMapper.readValue(
                        ex.contentUTF8(),
                        new TypeReference<ApiResponse<String>>() {}
                );
            } catch (JsonProcessingException e) {
                log.error("Failed to parse inventory response", e);
                throw new OrderException("Impossible de passer la commande : erreur inconnue d'inventaire");
            }

            // Log warning
            log.warn(inventoryResponse.getMessage());

            // Remonter une exception métier propre
            throw new OrderException(inventoryResponse.getMessage());
        }

        // 3️⃣ Créer l’entité Order
        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());
        order.setPrice(orderRequest.price());
        order.setSkuCode(orderRequest.skuCode());
        order.setQuantity(orderRequest.quantity());
        // order.setUserId(orderRequest.userDetails().getUserId());

        // 4️⃣ Sauvegarder dans la base
        orderRepository.save(order);

        // 5️⃣ Log succès
        log.info("✅ Order {} placed successfully for product {} qty {}", order.getOrderNumber(),
                order.getSkuCode(), order.getQuantity());
    }
}
