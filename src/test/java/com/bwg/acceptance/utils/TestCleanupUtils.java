package com.bwg.acceptance.utils;

import com.bwg.acceptance.config.TestContext;
import io.cucumber.spring.ScenarioScope;
import io.restassured.response.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.bwg.acceptance.utils.TestUtils.DOMAIN;
import static io.restassured.RestAssured.given;
@Component
@ScenarioScope
public class TestCleanupUtils {
    @Autowired
    TestContext testContext;

    private  String adminToken() {
        return "eyJhbGciOiJSUzI1NiIsImtpZCI6Ijg1NzA4MWNhOWNiYjM3YzIzNDk4ZGQzOTQzYmYzNzFhMDU4ODNkMjgiLCJ0eXAiOiJKV1QifQ.eyJpc3MiOiJodHRwczovL3NlY3VyZXRva2VuLmdvb2dsZS5jb20vYmxhY2t3ZWRkaW5nZ3VpZGUtYzQyM2MiLCJhdWQiOiJibGFja3dlZGRpbmdndWlkZS1jNDIzYyIsImF1dGhfdGltZSI6MTc0NDY1ODY0NCwidXNlcl9pZCI6IkdnekE2RGNhcG1kWFMxeGxQbzhoczZWQkZ6WjIiLCJzdWIiOiJHZ3pBNkRjYXBtZFhTMXhsUG84aHM2VkJGeloyIiwiaWF0IjoxNzQ0NjU4NjQ0LCJleHAiOjE3NDQ2NjIyNDQsImVtYWlsIjoiYWRpbHdhaGVlZDQ3NEBnbWFpbC5jb20iLCJlbWFpbF92ZXJpZmllZCI6ZmFsc2UsImZpcmViYXNlIjp7ImlkZW50aXRpZXMiOnsiZW1haWwiOlsiYWRpbHdhaGVlZDQ3NEBnbWFpbC5jb20iXX0sInNpZ25faW5fcHJvdmlkZXIiOiJwYXNzd29yZCJ9fQ.estimzVY3a05SRmnOKqOQdVr6wt7C7n7N4oOKH5j6Va-imTxBk0w1JvXkhbSjRQvFujQgWGlTn0p9sL7OD8olWz19e5eEe001peoaq5a0gcTAa4JKuWBprTO9uueYkSp04wSEQTZ0umHZvi_-PbmAoiWlkze3BM0ch2hSNI4WGU0J8FZ0hUPhEqQakML8YqrKXckEc4-TRYI3RUkpVMMQsut6zzKFxSQqG0ZtPkbW-am_O4KV8Tli62uS75beGzVSzbFGhfpRSuQgI2BlmbLyjy77EHiEwlCRGLyREm7lVKi0TwpDVAkKhzU8mJk_pZlg0olvyaIk5eQPoKk14eVeQ";
    }

    public  void deleteVendor(Long vendorId) {
        if (vendorId != null) {
            given()
                    .baseUri(DOMAIN)
                    .basePath("/vendors/{vendorId}")
                    .pathParam("vendorId", vendorId)
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + adminToken())
                    .delete();
        }
    }

    public void deleteService(Long serviceId) {
        if (serviceId != null) {
            given()
                    .baseUri(DOMAIN)
                    .basePath("/services/{id}")
                    .pathParam("id", serviceId)
                    .header("Authorization", testContext.getAuthToken())
                    .delete();
        }
    }

    public void deleteBooking(Long bookingId) {
        if (bookingId != null) {
            Response response = given()
                    .baseUri(TestUtils.DOMAIN)
                    .basePath("/bookings/{bookingId}")
                    .pathParam("bookingId", bookingId)
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + adminToken())
                    .delete();
            System.out.println("Delete response: " + response.getStatusCode() + " -> " + response.getBody().asString());
        }
    }

    public void deleteMedia(Long mediaId) {
        if (mediaId != null) {
            given()
                    .baseUri(DOMAIN)
                    .basePath("/media/{mediaId}")
                    .pathParam("mediaId", mediaId)
                    .header("Authorization", testContext.getAuthToken())
                    .delete();
        }
    }

    public void deleteUser(Long userId) {
        if (userId != null) {
            given()
                    .baseUri(DOMAIN)
                    .basePath("/users/{id}")
                    .pathParam("id", userId)
                    .header("Authorization", testContext.getAuthToken())
                    .delete();
        }
    }

    public void deleteTag(Long tagId) {
        if (tagId != null) {
            given()
                    .baseUri(DOMAIN)
                    .basePath("/tags/{tagId}")
                    .pathParam("tagId", tagId)
                    .header("Authorization", testContext.getAuthToken())
                    .delete();
        }
    }

    public void deleteCategory(Long categoryId) {
        if (categoryId != null) {
            given()
                    .baseUri(DOMAIN)
                    .basePath("/categories/{categoryId}")
                    .pathParam("categoryId", categoryId)
                    .header("Authorization", testContext.getAuthToken())
                    .delete();
        }
    }
}
