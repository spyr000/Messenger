package com.spyro.messenger.security.service;

import com.spyro.messenger.security.misc.TokenType;
import io.jsonwebtoken.Claims;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Map;
import java.util.function.Function;

public interface JwtService {
    String AUTH_HEADER_START_WITH = "Bearer ";
    String SESSION_ID_CLAIM_NAME = "ses";
    static String extractBearerToken(String authHeader) {
        return authHeader.substring(AUTH_HEADER_START_WITH.length());
    }
    String generateAuthToken(Map<String, Object> extraClaims, UserDetails userDetails, TokenType tokenType);

    String generateAuthToken(UserDetails userDetails, String sessionId, TokenType tokenType);

    boolean isAuthTokenValid(String token, UserDetails userDetails, long checksum, TokenType tokenType);
    boolean isAuthTokenValidForDisabledUser(String token, UserDetails userDetails, long checksum, TokenType tokenType);

    String extractUsername(String token, TokenType tokenType);

    String extractSessionID(String token, TokenType tokenType);

    <T> T extractClaim(String token, Function<Claims, T> claimsExtractor, TokenType tokenType);
}
