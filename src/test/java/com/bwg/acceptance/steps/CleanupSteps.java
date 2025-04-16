package com.bwg.acceptance.steps;

import com.bwg.acceptance.config.TestContext;
import com.bwg.acceptance.utils.TestCleanupUtils;
import io.cucumber.java.After;
import org.springframework.beans.factory.annotation.Autowired;
public class CleanupSteps {

    @Autowired
    private TestContext testContext;

    @Autowired
    TestCleanupUtils cleanupUtils;

    @After
    public void cleanUp() {
        Long categoryId = (Long) testContext.get("createdCategoryId");
        cleanupUtils.deleteVendor((Long) testContext.get("vendorId"));
        cleanupUtils.deleteBooking((Long) testContext.get("createdBookingId"));
        if(categoryId != null){
            cleanupUtils.deleteCategory((Long) testContext.get("categoryId"));
        }
        cleanupUtils.deleteService((Long) testContext.get("serviceId"));
        cleanupUtils.deleteUser((Long) testContext.get("userId"));
        cleanupUtils.deleteMedia((Long) testContext.get("mediaId"));
        cleanupUtils.deleteTag((Long) testContext.get("tagId"));
    }


}
