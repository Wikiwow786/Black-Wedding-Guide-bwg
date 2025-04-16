package com.bwg.acceptance.steps;

import com.bwg.acceptance.config.TestContext;
import com.bwg.acceptance.utils.TestUtils;
import io.cucumber.java.en.*;
import io.restassured.response.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.Map;
import static io.restassured.RestAssured.given;

@SpringBootTest
public class CategorySteps{

    @Autowired
    private TestContext testContext;

    @When("a user explores categories by searching for {string} and filtering with tag {string}")
    public void aUserExploresCategories(String search, String tagName) {
        Map<String, String> queryParams = new HashMap<>();
        if (search != null && !search.isBlank()) queryParams.put("search", search);
        if (tagName != null && !tagName.isBlank()) queryParams.put("tagName", tagName);

        Response response = given()
                .baseUri(TestUtils.DOMAIN)
                .basePath("/categories")
                .header("Content-Type", "application/json")
                .queryParams(queryParams)
                .when()
                .get();

        testContext.setResponse(response);
    }

    @When("a category with the name {string} is submitted")
    public void aCategoryWithTheNameIsSubmitted(String categoryName) {
        String requestBody = """
        {
          "category_name": "%s"
        }
        """.formatted(categoryName);

        Response response = given()
                .baseUri(TestUtils.DOMAIN)
                .basePath("/categories")
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + testContext.getAuthToken())
                .body(requestBody)
                .post();
        testContext.setResponse(response);
        if (response.getStatusCode() == 201) {
            Long categoryId = response.jsonPath().getLong("category_id");
            testContext.set("categoryId", categoryId);
        }
    }

    @When("a call happens to update category with the record id of {long} and category name of {string}")
    public void categoryUpdate(Long categoryId, String categoryName) {
        String requestBody = """
        {
          "category_name": "%s"
        }
        """.formatted(categoryName);

        Response response = given()
                .baseUri(TestUtils.DOMAIN)
                .basePath("/categories/{categoryId}")
                .pathParam("categoryId", categoryId)
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + testContext.getAuthToken())
                .body(requestBody)
                .put();
        testContext.setResponse(response);
    }

    @When("a call happens to delete category with the record id of {long}")
    public void deleteCategory(Long categoryId) {
        Response response = given()
                .baseUri(TestUtils.DOMAIN)
                .basePath("/categories/{categoryId}")
                .pathParam("categoryId", categoryId)
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + testContext.getAuthToken())
                .delete();
        testContext.setResponse(response);
    }

}


