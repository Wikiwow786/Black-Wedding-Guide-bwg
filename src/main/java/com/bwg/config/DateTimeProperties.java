package com.bwg.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DateTimeProperties {
    @Value("${datetime.format}")
    private String format;

    @Value("${datetime.timezone}")
    private String timezone;

    public String getFormat() {
        return format;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }
}
