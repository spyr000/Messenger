package com.spyro.messenger.security.deviceinfoparsing.service.impl;

import com.spyro.messenger.security.deviceinfoparsing.service.GeolocationService;
import com.spyro.messenger.security.service.HttpServletUtilsService;
import io.ipgeolocation.api.GeolocationParams;
import io.ipgeolocation.api.IPGeolocationAPI;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class GeolocationServiceImpl implements GeolocationService {
    private final IPGeolocationAPI api;
    @Override
    public String getCountryCity(String ip) {
        var geolocationParams =
                GeolocationParams.builder()
                        .withIPAddress(ip)
                        .withFields(GEOLOCATION_API_FIELDS)
                        .build();
        var geolocation = api.getGeolocation(geolocationParams);
        return new StringBuilder(geolocation.getCountryName())
                .append(", ")
                .append(geolocation.getCity())
                .toString();
    }

    @Override
    public String getCountryCity(HttpServletRequest request) {
        return getCountryCity(HttpServletUtilsService.getRequestIP(request));
    }
}
