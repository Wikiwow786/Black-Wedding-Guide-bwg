package com.bwg.acceptance.steps;

import com.bwg.acceptance.config.TestContext;
import com.bwg.acceptance.utils.TestUtils;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.When;
import io.restassured.response.Response;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;

public class BookingSteps {
    @Autowired
    private TestContext testContext;
    @When("a couple creates a booking with the following details:")
    public void createsBooking(DataTable dataTable) {
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
        if (response.getStatusCode() == 201) {
            Long bookingId = response.jsonPath().getLong("booking_id");
            testContext.set("createdBookingId", bookingId);
        }
    }

    @When("a call happens to update a booking with record id of {long} with the following details:")
    public void updatesBooking(Long bookingId,DataTable dataTable) {
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
                .basePath("/bookings/{bookingId}")
                .pathParam("bookingId",bookingId)
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + testContext.getAuthToken())
                .body(requestBody)
                .put();

        testContext.setResponse(response);
    }
    @When("a call happens to delete a booking with record id of {long}")
    public void deleteBooking(Long bookingId) {

        Response response = given()
                .baseUri(TestUtils.DOMAIN)
                .basePath("/bookings/{bookingId}")
                .pathParam("bookingId", bookingId)
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + testContext.getAuthToken())
                .delete();
        testContext.setResponse(response);
    }

    @When("the user looks up their bookings by entering {string} as a keyword and selecting status {string}")
    public void theUserLooksUpBookings(String search, String status) {
        Map<String, String> queryParams = new HashMap<>();
        if (search != null && !search.isBlank()) {
            queryParams.put("search", search);
        }
        if (status != null && !status.isBlank()) {
            queryParams.put("status", status);
        }

        Response response = given()
                .baseUri(TestUtils.DOMAIN)
                .basePath("/bookings")
                .header("Authorization", "Bearer " + testContext.getAuthToken())
                .header("Content-Type", "application/json")
                .queryParams(queryParams)
                .get();

        testContext.setResponse(response);
    }

}
