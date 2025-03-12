package com.bwg.config;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.TimeZone;

@Component
public class OffsetDateTimeCustomSerializer extends JsonSerializer<OffsetDateTime> {

    private static DateTimeProperties dateTimeProperties;

    @Autowired
    public OffsetDateTimeCustomSerializer(DateTimeProperties dateTimeProperties) {
        OffsetDateTimeCustomSerializer.dateTimeProperties = dateTimeProperties;
    }

    @Override
    public void serialize(OffsetDateTime value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        if (value != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(dateTimeProperties.getFormat())
                    .withZone(TimeZone.getTimeZone(dateTimeProperties.getTimezone()).toZoneId());
            gen.writeString(value.format(formatter));
        }
    }
}

