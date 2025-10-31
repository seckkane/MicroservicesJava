package com.techie.microservices.order;

import com.techie.microservices.order.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.testcontainers.containers.MySQLContainer;
import io.restassured.RestAssured;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;



@Import(TestcontainersConfiguration.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class OrderServiceApplicationTests {

    @Autowired
    private OrderRepository orderRepository; //Pour interagir avec la bdd

    @Autowired
    private JdbcTemplate jdbcTemplate; // pour executer une requete sql directement

    @ServiceConnection //fait le lien automatique entre ton conteneur Testcontainers et la configuration
    static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0.33")
            .withDatabaseName("order_service_test")
            .withUsername("testuser")
            .withPassword("testpass")
            .withReuse(true); // ⚡ permet de réutiliser le conteneur

    //C’est utilisé avec @SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
    @LocalServerPort //permet d’injecter automatiquement le port HTTP sur lequel ton application s’exécute pendant le test.
    private int port;

    @BeforeEach // Cette méthode est exécutée avant chaque test.
    void setup() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = port;
        //orderRepository.deleteAll();
        jdbcTemplate.execute("DELETE FROM t_orders");
    }

    static {
        if (!mysql.isRunning()) {
            mysql.start();
        }
    }

    @Test
    void containerShouldBeRunning() {
        assertThat(mysql.isRunning()).isTrue();
        System.out.println("--> MySQL Testcontainer running on: " + mysql.getJdbcUrl());
    }

    @Test
    void shouldCreateOrder() {
        String requestBody = """
            {
                "skuCode": "iphone_15",
                "price": 800.0,
                "quantity": 5
            }
            """;

        RestAssured
                .given()
                .contentType("application/json")
                .body(requestBody)
                .log().all() // affiche la requête
                .when()
                .post("/api/order") // port géré par @SpringBootTest
                .then()
                .log().all() // affiche la réponse
                .statusCode(201)
                .body(equalTo("Order Placed Successfully"));
    }

    @Test
    void shouldCreateOrderAndCheckDatabase() {
        // POST
        String requestBody = """
            {
                "skuCode": "iphone_15",
                "price": 800.0,
                "quantity": 5
            }
            """;

        RestAssured
                .given()
                .contentType("application/json")
                .body(requestBody)
                .when()
                .post("/api/order")
                .then()
                .statusCode(201)
                .body(equalTo("Order Placed Successfully"));

        // Vérification en base
        var orders = orderRepository.findAll();
        assertEquals(1, orders.size(), "Doit y avoir exactement 1 order");

        var order = orders.get(0);
        assertEquals("iphone_15", order.getSkuCode());
        assertEquals(800.0, order.getPrice().doubleValue());
        assertEquals(5, order.getQuantity());
    }


}