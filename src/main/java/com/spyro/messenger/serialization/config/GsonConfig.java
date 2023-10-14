package com.spyro.messenger.serialization.config;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.google.gson.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.spring.web.json.Json;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Configuration
public class GsonConfig {
    @Bean
    public JsonDeserializer<LocalDateTime> localDateJsonDeserializer() {
        return (json, type, context) -> LocalDateTime.parse(json.getAsString(), DateTimeFormatter.ISO_DATE_TIME);
    }

    @Bean
    public JsonSerializer<Json> springfoxJsonToGsonAdapter() {
        return (json, type, context) -> JsonParser.parseString(json.value());
    }

    @Bean
    public Gson gson() {
        var gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(LocalDateTime.class, localDateJsonDeserializer());
        gsonBuilder.registerTypeAdapter(Json.class, springfoxJsonToGsonAdapter());
        return gsonBuilder.setPrettyPrinting().create();
    }

}
