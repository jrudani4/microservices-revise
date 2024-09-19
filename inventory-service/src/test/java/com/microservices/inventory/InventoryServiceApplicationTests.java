package com.microservices.inventory;

import io.restassured.RestAssured;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.MySQLContainer;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class InventoryServiceApplicationTests {

    @SuppressWarnings("rawtypes")
    @ServiceConnection
    static MySQLContainer mySQLContainer = new MySQLContainer("mysql:8.3.0");

    @LocalServerPort
    private int port;

    @BeforeEach
    void setUp() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = port;
    }

    static {
        mySQLContainer.start();
    }

    @Test
    void shouldReadInventory() {
        var positiveResponse = RestAssured.given()
                .when()
                .get("/api/inventory?skuCode=iphone_15&quantity=78")
                .then()
                .log()
                .all()
                .statusCode(200)
                .extract()
                .response()
                .as(Boolean.class);
        Assertions.assertTrue(positiveResponse);

        var negativeResponse = RestAssured.given()
                .when()
                .get("/api/inventory?skuCode=iphone_15&quantity=101")
                .then()
                .log()
                .all()
                .statusCode(200)
                .extract()
                .response()
                .as(Boolean.class);
        Assertions.assertFalse(negativeResponse);
    }

}
