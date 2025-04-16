package com.bwg.acceptance.steps;

import com.bwg.acceptance.config.TestContext;
import com.bwg.acceptance.utils.TestUtils;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.And;
import io.cucumber.java.en.When;
import io.restassured.response.Response;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ServiceSteps{
    @Autowired
    private TestContext testContext;

    @When("a user searches for services with the following criteria:")
    public void aUserSearchesForServicesWithTheFollowingCriteria(DataTable dataTable) {
        Map<String, String> originalMap = dataTable.asMap(String.class, String.class);
        Map<String, String> filters = new HashMap<>(originalMap);
        filters.entrySet().removeIf(entry -> entry.getValue() == null || entry.getValue().isBlank());

        Response response = given()
                .baseUri(TestUtils.DOMAIN)
                .basePath("/services")
                .header("Content-Type", "application/json")
                .queryParams(filters)
                .when()
                .get();

        testContext.setResponse(response);
    }


    @When("the vendor or admin submits a new service with the following details:")
    public void submitNewService(DataTable dataTable) {
        Map<String, String> data = dataTable.asMap();

        String requestBody = """
        {
          "vendor_id": %s,
          "category_id": %s,
          "service_name": "%s",
          "description": "%s",
          "price_min": %s,
          "price_max": %s,
          "availability": "%s",
          "location": "%s"
        }
    """.formatted(
                data.get("vendor_id"),
                data.get("category_id"),
                data.get("service_name"),
                data.get("description"),
                data.get("price_min"),
                data.get("price_max"),
                data.get("availability"),
                data.get("location")
        );

        Response response = given()
                .baseUri(TestUtils.DOMAIN)
                .basePath("/services")
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + testContext.getAuthToken())
                .body(requestBody)
                .post();

        testContext.setResponse(response);
        if (response.getStatusCode() == 201) {
            Long serviceId = response.jsonPath().getLong("service_id");
            testContext.set("serviceId", serviceId);
        }

    }

    @When("the vendor or owner updates a service with record id of {long} with the following details:")
    public void updatesService(Long serviceId,DataTable dataTable) {
        Map<String, String> data = dataTable.asMap();

        String requestBody = """
        {
          "vendor_id": %s,
          "category_id": %s,
          "service_name": "%s",
          "description": "%s",
          "price_min": %s,
          "price_max": %s,
          "availability": "%s",
          "location": "%s"
        }
    """.formatted(
                data.get("vendor_id"),
                data.get("category_id"),
                data.get("service_name"),
                data.get("description"),
                data.get("price_min"),
                data.get("price_max"),
                data.get("availability"),
                data.get("location")
        );

        Response response = given()
                .baseUri(TestUtils.DOMAIN)
                .basePath("/services/{serviceId}")
                .pathParam("serviceId",serviceId)
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + testContext.getAuthToken())
                .body(requestBody)
                .put();

        testContext.setResponse(response);
    }

    @When("a call happens to delete service with the record id of {long}")
    public void deleteService(Long serviceId) {

        Response response = given()
                .baseUri(TestUtils.DOMAIN)
                .basePath("/services/{serviceId}")
                .pathParam("serviceId", serviceId)
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + testContext.getAuthToken())
                .delete();
        testContext.setResponse(response);
    }

    @And("the user selects a service named {string}")
    public void theUserSelectsAServiceNamed(String serviceName) {
        Response response = testContext.getResponse();
        System.out.println(response.getBody().asString());

        List<Map<String, Object>> services = response.jsonPath().getList("content");

        Optional<Long> matchedServiceId = services.stream()
                .filter(service -> serviceName.equalsIgnoreCase((String) service.get("service_name")))
                .map(service -> ((Number) service.get("service_id")).longValue())
                .findFirst();

        assertTrue(matchedServiceId.isPresent(), "Service not found in search results: " + serviceName);
        testContext.set("selectedServiceId", matchedServiceId.get());
    }


    @And("the user books the selected service using:")
    public void theUserBooksTheSelectedService(DataTable dataTable) {
        Map<String, String> data = dataTable.asMap();

        String requestBody = """
        {
          "user_id": %s,
          "service_id": %s,
          "event_date": "%s"
        }
    """.formatted(
                data.get("user_id"),
                data.get("service_id"),
                data.get("event_date")
        );

        Response response = given()
                .baseUri(TestUtils.DOMAIN)
                .basePath("/bookings")
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + testContext.getAuthToken())
                .body(requestBody)
                .post();

        testContext.setResponse(response);
        System.out.println("Booking Response: " + response.getBody().asString());
        if (response.getStatusCode() == 201) {
            Long bookingId = response.jsonPath().getLong("booking_id");
                testContext.set("createdBookingId", bookingId);
        } else {
            System.out.println("No booking_id found due to error status: " + response.getStatusCode());
        }
    }

    @When("the user searches for services using the keyword {string}")
    public void theUserSearchesForServicesUsingTheKeyword(String searchKeyword) {
        Response response = given()
                .baseUri(TestUtils.DOMAIN)
                .basePath("/services")
                .queryParam("search", searchKeyword)
                .header("Authorization", "Bearer " + testContext.getAuthToken())
                .get();

        testContext.setResponse(response);
    }


    @And("the user selects a service with ID {long} and name {string}")
    public void theUserSelectsAServiceWithIDAndName(Long serviceId, String serviceName) {
        testContext.set("selectedServiceId", serviceId);
        testContext.set("selectedServiceName", serviceName);
    }
}
