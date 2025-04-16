package com.bwg.acceptance.config;

import io.restassured.response.Response;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@Scope("cucumber-glue")
public class TestContext {
    private Response response;
    private String authToken;
    private final Map<String, Object> data = new HashMap<>();

    public Response getResponse() {
        return response;
    }

    public void setResponse(Response response) {
        this.response = response;
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public void set(String key, Object value) {
        data.put(key, value);
    }

    public <T> T get(String key, Class<T> type) {
        return type.cast(data.get(key));
    }

    public Object get(String key) {
        return data.get(key);
    }
}
