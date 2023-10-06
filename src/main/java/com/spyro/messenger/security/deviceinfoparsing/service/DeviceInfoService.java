package com.spyro.messenger.security.deviceinfoparsing.service;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;

public interface DeviceInfoService {
    String getDeviceInfo(HttpServletRequest request);

    long calculateHeadersChecksum(HttpServletRequest request);
}
