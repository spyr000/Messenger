package com.spyro.messenger.security.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface HttpServletUtilsService {
    ObjectMapper modifyResponseContentType(HttpServletRequest request, HttpServletResponse response, StringBuilder description);
}
