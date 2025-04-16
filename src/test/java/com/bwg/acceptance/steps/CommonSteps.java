package com.bwg.acceptance.steps;

import com.bwg.acceptance.config.TestContext;
import com.bwg.acceptance.utils.TestUtils;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;
import org.junit.jupiter.api.Assertions;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class CommonSteps {

    private final TestContext testContext;

    public CommonSteps(TestContext context) {
        this.testContext = context;
    }

    /*@Given("a logged-in user with the role {string}")
    public void anAuthenticatedUserWithRole(String role) {
        testContext.setAuthToken(TestUtils.generateRealJwtToken());
    }*/

    @Then("the system should confirm the action was {int}")
    public void theResponseStatusShouldBe(Integer expectedStatus) {
        Response response = testContext.getResponse();
        Assertions.assertEquals(expectedStatus, response.getStatusCode());
    }

    @Then("the user should see {string} in the result")
    public void theResponseShouldContainMessage(String expectedMessage) {
        Response response = testContext.getResponse();

        if ("content:[]".equals(expectedMessage)) {
            List<?> content = response.jsonPath().getList("content");
            assertTrue(content == null || content.isEmpty(), "Expected content to be empty, but got: " + content);
        } else {
            String body = response.getBody().asString().toLowerCase();
            assertTrue(body.contains(expectedMessage.toLowerCase()),
                    "Expected response to contain: '" + expectedMessage + "' but got: " + body);
        }
    }

   /* @When("a user is validated the specific role will be assigned {string}")
    public void validatedUserWithRole(String role) {
        testContext.setAuthToken(TestUtils.generateRealJwtToken());
    }*/
}
