package com.bwg.acceptance.steps;

import com.bwg.acceptance.config.TestContext;
import com.bwg.acceptance.utils.TestUtils;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.When;
import io.restassured.response.Response;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

import static io.restassured.RestAssured.given;

public class UserSteps {
    @Autowired
    private TestContext testContext;

    @When("the admin checks user accounts using:")
    public void adminViewsUsers(DataTable dataTable) {
        Map<String, String> queryParams = TestUtils.cleanAndExtractParams(dataTable);
        Response response = given()
                .baseUri(TestUtils.DOMAIN)
                .basePath("/users")
                .queryParams(queryParams)
                .header("Authorization", "Bearer " + testContext.getAuthToken())
                .get();

        testContext.setResponse(response);
    }

    @When("they view their personal profile information")
    public void viewPersonalProfileInfo() {
        Response response = given()
                .baseUri(TestUtils.DOMAIN)
                .basePath("/users/currentUser")
                .header("Authorization", "Bearer " + testContext.getAuthToken())
                .when()
                .get();

        testContext.setResponse(response);
    }

    @When("a call happens to update user details with id of {long} with the following details:")
    public void updatesUser(Long userId,DataTable dataTable) {
        Map<String, String> data = dataTable.asMap();

        String requestBody = """
        {
          "first_name": %s,
          "last_name": %s,
          "email": "%s",
          "phone_number": "%s"
        }
    """.formatted(
                data.get("first_name"),
                data.get("last_name"),
                data.get("email"),
                data.get("phone_number")
        );

        Response response = given()
                .baseUri(TestUtils.DOMAIN)
                .basePath("/users/{userId}")
                .pathParam("userId",userId)
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + testContext.getAuthToken())
                .body(requestBody)
                .put();

        testContext.setResponse(response);
    }

    @When("a call happens to delete user with the record id of {long}")
    public void deleteUser(Long userId) {

        Response response = given()
                .baseUri(TestUtils.DOMAIN)
                .basePath("/users/{userId}")
                .pathParam("userId", userId)
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + testContext.getAuthToken())
                .delete();
        testContext.setResponse(response);
    }
}