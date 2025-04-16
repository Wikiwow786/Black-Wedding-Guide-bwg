package com.bwg.acceptance.steps;

import com.bwg.acceptance.config.TestContext;
import com.bwg.acceptance.utils.TestUtils;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.When;
import io.restassured.response.Response;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

import static io.restassured.RestAssured.given;

public class TagSteps {
    @Autowired
    private TestContext testContext;

    @When("a user types {string} to explore available tags")
    public void userTypesToExploreTags(String keyword) {
        Response response = given()
                .baseUri(TestUtils.DOMAIN)
                .basePath("/tags")
                .queryParam("search", keyword)
                .header("Authorization", "Bearer " + testContext.getAuthToken())
                .get();

        testContext.setResponse(response);
    }

    @When("a call happens to add tags with the following details:")
    public void createTags(DataTable dataTable) {
        Map<String, String> data = dataTable.asMap();

        String requestBody = """
                    {
                      "tag_name": %s,
                      "status": %s
                    }
                """.formatted(
                data.get("tag_name"),
                data.get("status")
        );
        Response response = given()
                .baseUri(TestUtils.DOMAIN)
                .basePath("/tags")
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + testContext.getAuthToken())
                .body(requestBody)
                .post();

        testContext.setResponse(response);
        Long tagId = response.jsonPath().getLong("tag_id");
        testContext.set("tagId", tagId);
    }


    @When("the admin assigns the tag with ID {long} to the service with ID {long}")
    public void assignTagToService(Long tagId, Long serviceId) {
        Response response = given()
                .baseUri(TestUtils.DOMAIN)
                .basePath("/tags/{tagId}/service/{serviceId}")
                .pathParam("tagId", tagId)
                .pathParam("serviceId", serviceId)
                .header("Authorization", "Bearer " + testContext.getAuthToken())
                .when()
                .put();

        testContext.setResponse(response);
    }

    @When("the admin assigns the tag with ID {long} to the category with ID {long}")
    public void assignTagToCategory(Long tagId, Long categoryId) {
        Response response = given()
                .baseUri(TestUtils.DOMAIN)
                .basePath("/tags/{tagId}/service/{categoryId}")
                .pathParam("tagId", tagId)
                .pathParam("categoryId", categoryId)
                .header("Authorization", "Bearer " + testContext.getAuthToken())
                .when()
                .put();

        testContext.setResponse(response);
    }

    @When("a call happens to delete tag with the record id of {long}")
    public void deleteTag(Long tagId) {

        Response response = given()
                .baseUri(TestUtils.DOMAIN)
                .basePath("/tags/{tagId}")
                .pathParam("tagId", tagId)
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + testContext.getAuthToken())
                .delete();
        testContext.setResponse(response);
    }
}
