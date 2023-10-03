package com.spyro.messenger.security.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spyro.messenger.security.service.HttpServletUtilsService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


@Service
public class HttpServletUtilsServiceImpl implements HttpServletUtilsService {
    @Value("${spring.mvc.contentnegotiation.default-type}")
    private String defaultContentNegotiationType;
    @Value("${spring.mvc.contentnegotiation.parameter-name}")
    private String formatParameterName;
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    @Qualifier("xmlObjectMapper")
    private ObjectMapper xmlObjectMapper;

    @Autowired
    @Qualifier("jsonObjectMapper")
    private ObjectMapper jsonObjectMapper;

    @Override
    public ObjectMapper modifyResponseContentType(
            HttpServletRequest request,
            HttpServletResponse response,
            StringBuilder description
    ) {
        var queryString = request.getQueryString();
        description.append(request.getRequestURI());
        if (queryString != null) {
            description.append('?')
                    .append(queryString);
        }
        ObjectMapper exceptionResponseMapper = objectMapper;
        response.setContentType(defaultContentNegotiationType);
        if (queryString != null && queryString.startsWith(formatParameterName)) {
            switch (queryString.substring(formatParameterName.length() + 1)) {
                case ("xml") -> {
                    response.setContentType("application/xml");
                    exceptionResponseMapper = xmlObjectMapper;
                }
                case ("json") -> {
                    response.setContentType("application/json");
                    exceptionResponseMapper = jsonObjectMapper;
                }
            }
        }
        return exceptionResponseMapper;
    }
}
