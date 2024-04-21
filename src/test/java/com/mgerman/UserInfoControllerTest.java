package com.mgerman;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import static io.restassured.RestAssured.given;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserInfoControllerTest {

	@LocalServerPort
	private Integer port;

	@Test
	@Order(1)
	void getUserInfoTest() throws Exception {
		given()
				.auth()
				.basic("user1", "password")
				.contentType("application/json")
				.get("http://localhost:" + port + "/user-info")
				.then()
				.statusCode(200)
				.body("address", Matchers.equalTo("123 Example St, City, Country"));
	}

	@Test
	@Order(2)
	void updateUserInfoTest_updateAddress() throws Exception {
		given()
				.auth()
				.basic("user1", "password")
				.contentType("application/json")
				.body("""
						{
							"newAddress" : "new test address"
						}
						""")
				.post("http://localhost:" + port + "/user-info")
				.then()
				.statusCode(200)
				.body("address", Matchers.equalTo("new test address"));

		given()
				.auth()
				.basic("user1", "password")
				.contentType("application/json")
				.get("http://localhost:" + port + "/user-info")
				.then()
				.statusCode(200)
				.body("address", Matchers.equalTo("new test address"));
	}

	@Test
	@Order(3)
	void updateUserInfoTest_updatePassword() throws Exception {
		given()
				.auth()
				.basic("user1", "password")
				.contentType("application/json")
				.body("""
						{
							"newPassword" : "password1"
						}
						""")
				.post("http://localhost:" + port + "/user-info")
				.then()
				.statusCode(200)
				.body("address", Matchers.equalTo("new test address"));

		given()
				.auth()
				.basic("user1", "password1")
				.contentType("application/json")
				.get("http://localhost:" + port + "/user-info")
				.then()
				.statusCode(200)
				.body("address", Matchers.equalTo("new test address"));
	}
}