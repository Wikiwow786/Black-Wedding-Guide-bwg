package com.bwg.acceptance.steps;

import com.bwg.acceptance.config.TestContext;
import com.bwg.acceptance.utils.TestUtils;
import io.cucumber.java.en.*;
import io.restassured.response.Response;
import org.springframework.beans.factory.annotation.Autowired;
import static org.junit.jupiter.api.Assertions.*;

public class LoginSteps {
    @Autowired
    private TestContext testContext;


    private String email;
    private String password;


    @Given("a registered user with email {string} and password {string}")
    public void aRegisteredUserWithEmailAndPassword(String email, String password) {
        this.email = email;
        this.password = password;
    }

    @When("the user logs into the system with valid credentials")
    public void theUserLogsIntoTheSystemWithValidCredentials() {
        Response response = TestUtils.getLoginResponse(email,password);
        testContext.setResponse(response);
        testContext.setAuthToken(TestUtils.getToken(email,password));
    }


    @Then("the response should contain JWT")
    public void theResponseShouldContainJwt() {
        String idToken = testContext.getResponse().jsonPath().getString("idToken");
        assertNotNull(idToken, "Expected JWT token but got null");
    }

    @Then("the response should not contain")
    public void theResponseShouldNotContain() {
        String idToken = testContext.getResponse().jsonPath().getString("idToken");
        assertNull(idToken, "Expected no JWT token but got: " + idToken);
    }



}
