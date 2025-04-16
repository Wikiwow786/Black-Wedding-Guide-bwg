package com.bwg.acceptance.steps;

import com.bwg.acceptance.config.TestContext;
import com.bwg.acceptance.utils.TestUtils;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

public class MediaSteps {
    @Autowired
    private TestContext testContext;
    @When("a media file is uploaded with:")
    public void uploadMediaWithDetails(DataTable dataTable) {
        Map<String, String> formData = dataTable.asMap();
        String title = formData.getOrDefault("Upload Title", "");
        String entityType = formData.getOrDefault("Related To", "");
        String entityIdStr = formData.get("Record ID");
        Long entityId = entityIdStr != null && !entityIdStr.isBlank() ? Long.parseLong(entityIdStr) : null;
        File testFile = new File("src/test/resources/test-image.webp");
        assertTrue(testFile.exists(), "Test file does not exist: " + testFile.getAbsolutePath());

        Response response = given()
                .baseUri(TestUtils.DOMAIN)
                .basePath("/media")
                .header("Authorization", "Bearer " + testContext.getAuthToken())
                .multiPart("file", testFile)
                .multiPart("title", title)
                .multiPart("entityType", entityType)
                .multiPart("entityId", entityId != null ? entityId.toString() : "")
                .accept(ContentType.JSON)
                .when()
                .post();
        testContext.setResponse(response);
    }

    @When("the user requests to view media associated with that record")
    public void theUserRequestsToViewMediaAssociatedWithThatRecord() {
        Long entityId = testContext.get("entityId", Long.class);
        Response response = given()
                .baseUri(TestUtils.DOMAIN)
                .basePath("/media/entity/{entityId}")
                .pathParam("entityId", entityId)
                .header("Authorization", "Bearer " + testContext.getAuthToken())
                .when()
                .get();

        testContext.setResponse(response);
    }

    @Then("the system should return a list of media")
    public void theSystemShouldReturnAListOfMedia() {
        Response response = testContext.getResponse();
        assertEquals(200, response.statusCode(), "Expected 200 OK response");
    }

    @Then("the response should contain {string}")
    public void theResponseShouldContain(String expectedContent) {
        String body = testContext.getResponse().getBody().asString();
        if ("[]".equals(expectedContent)) {
            assertTrue(body.contains("[]"), "Expected empty media list but got: " + body);
        } else {
            assertTrue(body.contains(expectedContent), "Expected media to contain: " + expectedContent);
        }
    }

    @Given("a record exists in the system with ID {long}")
    public void aRecordExistsInTheSystemWithId(Long entityId) {
        testContext.set("entityId", entityId);
    }

    @When("the user attempts to download the media file with ID {long}")
    public void theUserAttemptsToDownloadTheMediaFileWithId(Long mediaId) {
        Response response = given()
                .baseUri(TestUtils.DOMAIN)
                .basePath("/media/{mediaId}/download")
                .pathParam("mediaId", mediaId)
                .header("Authorization", "Bearer " + testContext.getAuthToken())
                .get();

        testContext.setResponse(response);
    }

    @Then("the response should contain the file bytes")
    public void theResponseShouldContainTheFileBytes() {
        Response response = testContext.getResponse();

        byte[] content = response.getBody().asByteArray();

        if (response.getStatusCode() == 200) {
            assertNotNull(content, "Expected file bytes but got null");
            assertTrue(content.length > 0, "Expected file content but body is empty");
        } else {
            assertEquals(0, content.length, "Expected no file content for non-200 status");
        }
    }

    @When("a call happens to delete media with the record id of {long}")
    public void deleteMedia(Long mediaId) {

        Response response = given()
                .baseUri(TestUtils.DOMAIN)
                .basePath("/media/{mediaId}")
                .pathParam("mediaId", mediaId)
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + testContext.getAuthToken())
                .delete();
        testContext.setResponse(response);
    }

}
