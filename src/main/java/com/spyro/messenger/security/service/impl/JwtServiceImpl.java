package com.spyro.messenger.security.service.impl;


import com.spyro.messenger.exceptionhandling.exception.BaseException;
import com.spyro.messenger.security.misc.TokenType;
import com.spyro.messenger.security.service.JwtService;
import com.spyro.messenger.security.service.SessionService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

@Service
@Setter
@RequiredArgsConstructor
@Slf4j
public class JwtServiceImpl implements JwtService {
    private final Environment env;
    private final SessionService sessionService;

    //CHECKED
    @Override
    public String generateAuthToken(
            Map<String, Object> extraClaims,
            UserDetails userDetails,
            TokenType tokenType
    ) {
        if (tokenType == TokenType.CONFIRMATION) {
            //TODO:
            throw new BaseException(HttpStatus.BAD_REQUEST,"Unsupported type of token");
        }
        String expirationTime = tokenType.equals(TokenType.ACCESS)
                ? env.getProperty("security.encryption.access-token.expiration-time")
                : env.getProperty("security.encryption.refresh-token.expiration-time");
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
    public String generateAuthToken(UserDetails userDetails, String sessionId, TokenType tokenType) {
        return generateAuthToken(Map.of(this.SESSION_ID_CLAIM_NAME, sessionId), userDetails, tokenType);
    }

    @Override
    public boolean isAuthTokenValid(String token, UserDetails userDetails, long checksum, TokenType tokenType) {
        final var username = extractUsername(token, tokenType);
        final var sessionId = extractSessionID(token, tokenType);
        return (username.equals(userDetails.getUsername())) &&
                !isTokenExpired(token, tokenType) &&
                userDetails.isEnabled() &&
                sessionService.isActive(sessionId, userDetails,
                        checksum,
                        (foundSession) -> {/*TODO:*/log.warn("Session %s checksum mismatch: %s".formatted(foundSession,checksum)); }
                );
    }

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
    public String extractSessionID(String token, TokenType tokenType) {
        return extractClaim(token, claims -> (String) claims.get(this.SESSION_ID_CLAIM_NAME), tokenType);
    }

    @Override
    public <T> T extractClaim(String token, Function<Claims, T> claimsExtractor, TokenType tokenType) {
        final Claims claims = extractAllClaims(token, tokenType);
        return claimsExtractor.apply(claims);
    }

    private Key getSignInKey(TokenType tokenType) {
        String signingKey;
        if (tokenType == TokenType.ACCESS) {
            signingKey = env.getProperty("security.encryption.access-token.signing-key");
        } else if (tokenType == TokenType.REFRESH) {
            signingKey = env.getProperty("security.encryption.refresh-token.signing-key");
        } else {
            signingKey = env.getProperty("security.encryption.confirmation-token.signing-key");
        }
        return Keys.hmacShaKeyFor(
                Decoders
                        .BASE64
                        .decode(Objects.requireNonNull(signingKey))
        );
    }
}
