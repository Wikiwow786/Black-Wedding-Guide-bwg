package com.bwg.unit.config;

import com.bwg.config.DateTimeProperties;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
@TestConfiguration
public class TestConfig {
    @Bean
    public DateTimeProperties dateTimeProperties() {
        DateTimeProperties props = new DateTimeProperties();
        props.setFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
        props.setTimezone("UTC");
        return props;
    }
}
