package com.bwg.acceptance.runner;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
        features = "src/test/java/com/bwg/acceptance/features",
        glue = {
                "com.bwg.acceptance.steps",
                "com.bwg.acceptance.config"
        },
        plugin = {"pretty", "html:target/cucumber-report.html"},
        publish = true
)
public class CucumberTest {
}
