package com.spyro.messenger.security.service;

import jakarta.servlet.http.HttpServletRequest;

public interface DeviceInfoService {
    long calculateHeadersChecksum(HttpServletRequest request);
}
