package com.prashanth.microservices.product;

import io.restassured.RestAssured;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Import;
import org.testcontainers.containers.MongoDBContainer;

//@Import(TestcontainersConfiguration.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)// Generates random ports for the test to prevent it from colliding with default port
class ProductServiceApplicationTests {

	@ServiceConnection//assigns the mongoDB URI automatically from the application.properties
	//Create a mongoDB container of image mongo:7.0.5
	static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:7.0.5");
	@LocalServerPort
	private int port;//capture the local server pot and store it in the port variable

	//setting up a local server port for the rest assured
	@BeforeEach
	void setup(){
		RestAssured.baseURI = "http://localhost";
		RestAssured.port = port;
	}

	//Starting a test container before the test
	static{
		mongoDBContainer.start();
	}

	@Test
	void shouldAddProduct() {
		String requestBody = """
				{
				    "name":"Polo Shirts",
				    "description": "Colour premium shirts",
				    "price":1000
				}""";
		RestAssured.given()
				.contentType("application/json")
				.body(requestBody)
				.when()
				.post("/api/product")
				.then()
				.statusCode(201)
				.body("id", Matchers.notNullValue())
				.body("name", Matchers.equalTo("Polo Shirts"))
				.body("description", Matchers.equalTo("Colour premium shirts"))
				.body("price", Matchers.equalTo(1000));
	}

}
