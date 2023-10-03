package com.spyro.messenger.security.filter;


import com.spyro.messenger.exceptionhandling.exception.ErrorMessage;
import com.spyro.messenger.exceptionhandling.exception.ParseJwtException;
import com.spyro.messenger.security.misc.TokenType;
import com.spyro.messenger.security.service.HttpServletUtilsService;
import com.spyro.messenger.security.service.JwtService;
import com.spyro.messenger.user.repo.repo.UserRepo;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.regex.Pattern;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final HttpServletUtilsService httpServletUtilsService;

    private static final String AUTH_HEADER_START_WITH = "Bearer ";

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws IOException {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        final String jwtToken;
        final String username;
        try {
            if (authHeader == null || !authHeader.startsWith(AUTH_HEADER_START_WITH)) {
                filterChain.doFilter(request, response);
                return;
            }
            jwtToken = authHeader.substring(AUTH_HEADER_START_WITH.length());
            var pattern = Pattern.compile("\\..+\\.");
            var matcher = pattern.matcher(jwtToken);
            String payload;
            if (matcher.find()) {
                payload = new String(Base64.getDecoder().decode(jwtToken.substring(matcher.start() + 1, matcher.end() - 1)));
                System.out.println(payload);
            } else throw new ParseJwtException(HttpStatus.BAD_REQUEST, "Could not parse auth JWT");

            username = jwtService.extractUsername(jwtToken, TokenType.ACCESS);
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                var userDetails = this.userDetailsService.loadUserByUsername(username);
                if (jwtService.isTokenValid(jwtToken, userDetails, TokenType.ACCESS)) {
                    var authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );
                    authToken.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request)
                    );
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
            filterChain.doFilter(request, response);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            var description = new StringBuilder();
            var exceptionResponseMapper = httpServletUtilsService.modifyResponseContentType(request, response, description);
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            try {
                response.getWriter().write(
                        exceptionResponseMapper.writerWithDefaultPrettyPrinter().writeValueAsString(
                                ErrorMessage.builder()
                                        .message(e.getMessage())
                                        .description(description.toString())
                                        .statusCode(HttpStatus.UNAUTHORIZED.value())
                                        .time(LocalDateTime.now())
                                        .build()
                        )
                );
            } catch (IOException ex) {
                log.error(ex.getMessage(), ex);
                exceptionResponseMapper = httpServletUtilsService.modifyResponseContentType(request, response, description);
                response.setStatus(HttpStatus.BAD_REQUEST.value());
                response.getWriter().write(
                        exceptionResponseMapper.writerWithDefaultPrettyPrinter().writeValueAsString(
                                ErrorMessage.builder()
                                        .message(ex.getMessage())
                                        .description(description.toString())
                                        .statusCode(HttpStatus.BAD_REQUEST.value())
                                        .time(LocalDateTime.now())
                                        .build()
                        )
                );
            }

        }

    }
}
