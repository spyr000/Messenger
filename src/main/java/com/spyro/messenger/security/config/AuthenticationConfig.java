package com.spyro.messenger.security.config;


import com.spyro.messenger.exceptionhandling.dto.ErrorMessage;
import com.spyro.messenger.security.service.HttpServletUtilsService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;

import java.time.LocalDateTime;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class AuthenticationConfig {
    private final UserDetailsService userDetailService;
    private final HttpServletUtilsService httpServletUtilsService;

    @Bean
    public AuthenticationProvider authenticationProvider() {
        var authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    AuthenticationEntryPoint authenticationEntryPoint() {
        return (request, response, authException) -> {
            var description = new StringBuilder();
            log.error(authException.getMessage(), authException);
            var exceptionResponseMapper = httpServletUtilsService.modifyResponseContentType(request, response, description);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write(
                    exceptionResponseMapper.writerWithDefaultPrettyPrinter().writeValueAsString(
                            ErrorMessage.builder()
                                    .message(authException.getMessage())
                                    .description(description.toString())
                                    .statusCode(HttpStatus.UNAUTHORIZED.value())
                                    .time(LocalDateTime.now().toString())
                                    .build()
                    )
            );
        };
    }

    @Bean
    AccessDeniedHandler accessDeniedHandler() {
        return (request, response, accessDeniedException) -> {
            log.error(accessDeniedException.getMessage(), accessDeniedException);
            var description = new StringBuilder();
            var exceptionResponseMapper = httpServletUtilsService.modifyResponseContentType(request, response, description);
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().write(
                    exceptionResponseMapper.writerWithDefaultPrettyPrinter().writeValueAsString(
                            ErrorMessage.builder()
                                    .message(accessDeniedException.getMessage())
                                    .description(description.toString())
                                    .statusCode(HttpStatus.FORBIDDEN.value())
                                    .time(LocalDateTime.now().toString())
                                    .build()
                    ));
        };
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
