package com.spyro.messenger.security.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import eu.bitwalker.useragentutils.UserAgent;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import ua_parser.Client;
import ua_parser.Parser;

import java.util.HashMap;
import java.util.Map;

public interface HttpServletUtilsService {
    ObjectMapper modifyResponseContentType(HttpServletRequest request, HttpServletResponse response, StringBuilder description);

    String[] IP_HEADERS = {
            "X-Forwarded-For",
            "Proxy-Client-IP",
            "WL-Proxy-Client-IP",
            "HTTP_X_FORWARDED_FOR",
            "HTTP_X_FORWARDED",
            "HTTP_X_CLUSTER_CLIENT_IP",
            "HTTP_CLIENT_IP",
            "HTTP_FORWARDED_FOR",
            "HTTP_FORWARDED",
            "HTTP_VIA",
            "REMOTE_ADDR",
    };

    String[] CHECKSUM_HEADERS = {
            HttpHeaders.USER_AGENT,
            HttpHeaders.ACCEPT,
            HttpHeaders.ACCEPT_ENCODING,
            HttpHeaders.ACCEPT_LANGUAGE
    };

    static String getRequestIP(HttpServletRequest request) {
        for (String header : IP_HEADERS) {
            String value = request.getHeader(header);
            if (value == null || value.isEmpty()) {
                continue;
            }
            String[] parts = value.split("\\s*,\\s*");
            return parts[0];
        }
        return request.getRemoteAddr();
    }

    static String getUAInformation(HttpServletRequest request) {
        var header = request.getHeader(HttpHeaders.USER_AGENT);
        Client c = new Parser().parse(header);
        var userAgent = c.userAgent;
        var os = c.os;
        System.out.println(new StringBuilder(userAgent.family).append('/').append(userAgent.major).append('.').append(userAgent.minor).append(", ").append(os.family).append(' ').append(os.major).append(", ").append(c.device.family));

        return new StringBuilder(userAgent.family)
                .append('/')
                .append(userAgent.major)
                .append('.')
                .append(userAgent.minor)
                .append(", ")
                .append(os.family)
                .append(' ')
                .append(os.major)
                .append(", ")
                .append(c.device.family)
                .toString();
    }

    static Map<String,String> getChecksumHeaders(HttpServletRequest request) {
        Map<String, String> headers = new HashMap<>();
        for (String headerName: CHECKSUM_HEADERS) {
            headers.put(headerName,request.getHeader(headerName));
        }
        return headers;
    }
}
