package com.spyro.messenger.swagger.config;

import com.google.gson.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.spring.web.json.Json;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Configuration
public class GsonConfig {
    @Bean
    JsonDeserializer<LocalDateTime> localDateJsonDeserializer() {
        return (jsonElement, type, jsonDeserializationContext) -> LocalDateTime.parse(jsonElement.getAsString(), DateTimeFormatter.ISO_DATE_TIME);
    }

    @Bean
    JsonSerializer<Json> springfoxJsonToGsonAdapter() {
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
