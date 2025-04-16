package com.bwg.acceptance.steps;

import com.bwg.acceptance.config.TestContext;
import com.bwg.acceptance.utils.TestUtils;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.When;
import io.restassured.response.Response;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

import static io.restassured.RestAssured.given;

public class VendorSteps {
    @Autowired
    private TestContext testContext;

    @When("a user types {string} to explore available vendors")
    public void userTypesToExploreVendors(String keyword) {
        Response response = given()
                .baseUri(TestUtils.DOMAIN)
                .basePath("/vendors")
                .queryParam("search", keyword)
               // .header("Authorization", "Bearer " + testContext.getAuthToken())
                .get();

        testContext.setResponse(response);
    }


    @When("a vendor submits a record with the following details:")
    public void createVendorRecord(DataTable dataTable) {
        Map<String, String> data = dataTable.asMap();

        String requestBody = """
        {
          "user_id": %s,
          "business_name": %s,
          "description": "%s",
          "rating": %s,
          "location": "%s"
        }
    """.formatted(
                data.get("user_id"),
                data.get("business_name"),
                data.get("description"),
                data.get("rating"),
                data.get("location")
        );

        Response response = given()
                .baseUri(TestUtils.DOMAIN)
                .basePath("/vendors")
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + testContext.getAuthToken())
                .body(requestBody)
                .post();

        testContext.setResponse(response);
        Long vendorId = response.jsonPath().getLong("vendor_id");
        testContext.set("vendorId", vendorId);
    }

    @When("a call happens to update business details with id of {long} with the following details:")
    public void updateVendorRecord(Long vendorId,DataTable dataTable) {
        Map<String, String> data = dataTable.asMap();

        String requestBody = """
        {
          "business_name": %s,
          "description": "%s",
          "total_reviews": %s,
          "location": "%s"
        }
    """.formatted(
                data.get("business_name"),
                data.get("description"),
                data.get("total_reviews"),
                data.get("location")
        );

        Response response = given()
                .baseUri(TestUtils.DOMAIN)
                .basePath("/vendors/{vendorId}")
                .pathParam("vendorId",vendorId)
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + testContext.getAuthToken())
                .body(requestBody)
                .put();

        testContext.setResponse(response);
    }

    @When("a call happens to delete vendor with the record id of {long}")
    public void deleteVendor(Long vendorId) {

        Response response = given()
                .baseUri(TestUtils.DOMAIN)
                .basePath("/vendors/{vendorId}")
                .pathParam("vendorId", vendorId)
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + testContext.getAuthToken())
                .delete();
        testContext.setResponse(response);
    }
}
