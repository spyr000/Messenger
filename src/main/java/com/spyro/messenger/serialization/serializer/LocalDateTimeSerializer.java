package com.spyro.messenger.serialization.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.time.LocalDateTime;

public class LocalDateTimeSerializer extends JsonSerializer<LocalDateTime> {
    @Override
    public void serialize(LocalDateTime time,
                          JsonGenerator jsonGenerator,
                          SerializerProvider serializerProvider
    ) throws IOException {
        jsonGenerator.writeString(time.toString());
    }
}
