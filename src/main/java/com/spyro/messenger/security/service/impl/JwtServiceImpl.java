package com.spyro.messenger.security.service.impl;


import com.spyro.messenger.security.misc.TokenType;
import com.spyro.messenger.security.service.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.core.env.Environment;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

@Service
@Setter
@RequiredArgsConstructor
public class JwtServiceImpl implements JwtService {
    private final Environment env;

    @Override
    public String generateToken(
            Map<String, Object> extraClaims,
            UserDetails userDetails,
            TokenType tokenType
    ) {
        String expirationTime = tokenType.equals(TokenType.ACCESS)
                ? env.getProperty("security.encryption.token-expiration-time-millis.access")
                : env.getProperty("security.encryption.token-expiration-time-millis.refresh");
        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(
                        new Date(
                                System.currentTimeMillis()
                                        + Integer.parseInt(
                                        Objects.requireNonNull(expirationTime)
                                )
                        )
                )
                .signWith(getSignInKey(tokenType), SignatureAlgorithm.HS256)
                .compact();
    }

    @Override
    public String generateToken(UserDetails userDetails, TokenType tokenType) {
        return generateToken(new HashMap<>(), userDetails, tokenType);
    }

    @Override
    public boolean isTokenValid(String token, UserDetails userDetails, TokenType tokenType) {
        final String username = extractUsername(token, tokenType);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token, tokenType) && userDetails.isEnabled();
    }

//    @Override
//    public boolean isUserHaveAccessToProject(String authHeader, Project project) {
//        var tokenStartIndex = 7;
//        var token = authHeader.substring(tokenStartIndex);
//        var tokenUsername = extractUsername(token, TokenType.ACCESS);
//        var dbUsername = project.getUser().getUsername();
//        return tokenUsername.equals(dbUsername);
//    }

    private boolean isTokenExpired(String token, TokenType tokenType) {
        return extractExpiration(token, tokenType).before(new Date());
    }

    private Date extractExpiration(String token, TokenType tokenType) {
        return extractClaim(token, Claims::getExpiration, tokenType);
    }

    private Claims extractAllClaims(String token, TokenType tokenType) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignInKey(tokenType))
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    @Override
    public String extractUsername(String token, TokenType tokenType) {
        return extractClaim(token, Claims::getSubject, tokenType);
    }

    @Override
    public <T> T extractClaim(String token, Function<Claims, T> claimsExtractor, TokenType tokenType) {
        final Claims claims = extractAllClaims(token, tokenType);
        return claimsExtractor.apply(claims);
    }

    private Key getSignInKey(TokenType tokenType) {
        String encryptionKey = tokenType.equals(TokenType.ACCESS)
                ? env.getProperty("security.encryption.token-key.access")
                : env.getProperty("security.encryption.token-key.refresh");

        return Keys.hmacShaKeyFor(
                Decoders
                        .BASE64
                        .decode(Objects.requireNonNull(encryptionKey))
        );
    }

//    @Override
//    public boolean isAuthHeaderSuitable(String authHeader) {
//        return authHeader != null && authHeader.startsWith("Bearer ");
//    }
}
