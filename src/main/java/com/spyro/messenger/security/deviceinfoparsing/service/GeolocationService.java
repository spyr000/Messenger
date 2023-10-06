package com.spyro.messenger.security.deviceinfoparsing.service;

import jakarta.servlet.http.HttpServletRequest;

public interface GeolocationService {
    String GEOLOCATION_API_FIELDS = "geo";
    String getCountryCity(String ip);

    String getCountryCity(HttpServletRequest request);
}
