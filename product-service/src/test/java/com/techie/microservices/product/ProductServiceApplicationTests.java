package com.techie.microservices.product;

import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Import;
import org.testcontainers.containers.MongoDBContainer;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;


@Import(TestcontainersConfiguration.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ProductServiceApplicationTests {

    @ServiceConnection
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:7.0.5");

    @LocalServerPort
    private Integer port;

    @BeforeEach // Elle indique que cette méthode doit s’exécuter avant chaque test de la classe.
    void setup() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = port;
    }

    static { // Exécuté une seule fois, au moment où la classe est chargée en mémoire (avant toute méthode ou tout test).
        mongoDBContainer.start();
    }

	@Test
	void shouldCreateProduct() {
        String requestBody = """
                {
                    "name": "Casque enfant",
                    "description": "Casque Bluetooth avec réduction de bruit active",
                    "skuCode": "SKU-HDP789",
                    "price": 200.00
                }
                """;
        RestAssured
                .given()
                    .contentType("application/json")
                    .body(requestBody)
                .when()
                    .post("/api/product")
                .then()
                    .statusCode(201)
                    .body("id", notNullValue())
                    .body("name", equalTo("Casque enfant"))
                    .body("description", equalTo("Casque Bluetooth avec réduction de bruit active"))
                    .body("skuCode", equalTo("SKU-HDP789"))
                    .body("price", equalTo(200.0f)); // important

    }

}
