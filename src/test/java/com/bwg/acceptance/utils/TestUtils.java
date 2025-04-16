package com.bwg.acceptance.utils;

import io.cucumber.datatable.DataTable;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static io.restassured.RestAssured.given;

public class TestUtils {
    public static String DOMAIN = System.getenv("bwg_base_url") != null ?
            System.getenv("bwg_base_url") : "http://localhost:8080/api/v1";

    private static final Map<String, String> tokenCache = new HashMap<>();

    /*public static String generateRealJwtToken() {
        return "eyJhbGciOiJSUzI1NiIsImtpZCI6Ijg1NzA4MWNhOWNiYjM3YzIzNDk4ZGQzOTQzYmYzNzFhMDU4ODNkMjgiLCJ0eXAiOiJKV1QifQ.eyJpc3MiOiJodHRwczovL3NlY3VyZXRva2VuLmdvb2dsZS5jb20vYmxhY2t3ZWRkaW5nZ3VpZGUtYzQyM2MiLCJhdWQiOiJibGFja3dlZGRpbmdndWlkZS1jNDIzYyIsImF1dGhfdGltZSI6MTc0NDY0NzUxMywidXNlcl9pZCI6IlVjT01vSHFIWWNVeUNxSzl3cVdjdDI2YlZPbTEiLCJzdWIiOiJVY09Nb0hxSFljVXlDcUs5d3FXY3QyNmJWT20xIiwiaWF0IjoxNzQ0NjQ3NTEzLCJleHAiOjE3NDQ2NTExMTMsImVtYWlsIjoiYWRpbC5jb3VwbGVAZ21haWwuY29tIiwiZW1haWxfdmVyaWZpZWQiOmZhbHNlLCJmaXJlYmFzZSI6eyJpZGVudGl0aWVzIjp7ImVtYWlsIjpbImFkaWwuY291cGxlQGdtYWlsLmNvbSJdfSwic2lnbl9pbl9wcm92aWRlciI6InBhc3N3b3JkIn19.ZIbjJdGC5cW63qrtUpZdaXF772-IrGHj1C0CykKtiMtjuQh1oprKIFaRnDOC1J637RI7Bkvq1mZuALQmRbFCLw9HXsFafQHW8Q5jcfjasZ7JTVNednjnqwKrBQHBfk1IvDjBMc1yJyZKznhzOuOm_6YrcJDo0u0KmY9FED9Mya4PhlYFuIvgdmmxQTZttgVkB8oZh6a_VLVTPU9jGgdKYreNDc3EvId6zl67YkQEO20QpCGPYHzSbWkocEuCoX7mEH9UFF1uAI8fMmHeGmpnccvBnsx2PaIZJME20V21cDA7esNYQLL52xCnHKk_jaQbqy_txDHU5KjX2EhQGTVSjw";
    }*/

    public static Map<String, String> cleanAndExtractParams(DataTable dataTable) {
        Map<String, String> allParams = dataTable.asMap(String.class, String.class);
        return allParams.entrySet().stream()
                .filter(entry -> entry.getValue() != null && !entry.getValue().isBlank())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
    public static String getToken(String email, String password) {
        return tokenCache.computeIfAbsent(email, e ->
                getLoginResponse(email, password).jsonPath().getString("idToken")
        );
    }

    public static Response getLoginResponse(String email, String password) {
        RequestSpecification spec = new RequestSpecBuilder()
                .setUrlEncodingEnabled(false)
                .setBaseUri("https://identitytoolkit.googleapis.com")
                .build();

        String apiKey = "AIzaSyAitCCsBTueKV5AcltoEKqUe6REo-Yny2k";
        String endpoint = "/v1/accounts:signInWithPassword?key=" + apiKey;
        String requestBody = """
    {
      "email": "%s",
      "password": "%s",
      "returnSecureToken": true
    }
    """.formatted(email, password);

        return given()
                .spec(spec)
                .contentType("application/json")
                .body(requestBody)
                .post(endpoint)
                .then()
                .log().all()
                .extract()
                .response();
    }

}

