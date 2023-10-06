package com.spyro.messenger.security.deviceinfoparsing.config;

import io.ipgeolocation.api.IPGeolocationAPI;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GeolocationConfig {

    @Bean
    public IPGeolocationAPI ipGeolocationAPI(@Value("${security.geolocation-api.api-key}") String apiKey) {
        return new IPGeolocationAPI(apiKey);
    }
}
