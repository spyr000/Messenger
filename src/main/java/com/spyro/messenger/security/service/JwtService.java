package com.spyro.messenger.security.service;

import com.spyro.messenger.security.misc.TokenType;
import io.jsonwebtoken.Claims;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Map;
import java.util.function.Function;

public interface JwtService {
    String generateToken(Map<String, Object> extraClaims, UserDetails userDetails, TokenType tokenType);

    String generateToken(UserDetails userDetails, TokenType tokenType);

    boolean isTokenValid(String token, UserDetails userDetails, TokenType tokenType);

    String extractUsername(String token, TokenType tokenType);

    <T> T extractClaim(String token, Function<Claims, T> claimsExtractor, TokenType tokenType);
}
