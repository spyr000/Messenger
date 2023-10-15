package com.spyro.messenger.security.config;

import com.spyro.messenger.emailverification.util.ConfirmationUrls;
import com.spyro.messenger.security.filter.JwtAuthenticationFilter;
import jakarta.servlet.ServletException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.springframework.security.config.Customizer.withDefaults;

@Slf4j
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationEntryPoint authenticationEntryPoint;
    private final AccessDeniedHandler accessDeniedHandler;
    private final AuthenticationProvider authenticationProvider;

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        var configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("*"));
        configuration.addAllowedHeader("*");
        configuration.addAllowedMethod("*");
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .cors()
                .and()
                .exceptionHandling()
                .accessDeniedHandler(accessDeniedHandler)
                .authenticationEntryPoint((request, response, authException) -> {
                    try {
                        Optional.of(request.getHeader(HttpHeaders.AUTHORIZATION))
                                .ifPresent(
                                        (req) -> {
                                            if (req.isBlank()) {
                                                try {
                                                    authenticationEntryPoint.commence(request, response, authException);
                                                } catch (IOException | ServletException e) {
                                                    throw new RuntimeException(e);
                                                }
                                            }

                                        }
                                );
                    } catch (NullPointerException e) {
                        authenticationEntryPoint.commence(request, response, authException);
                    }
                })
                .and()
                .authorizeHttpRequests(
                        (requests) -> requests
                                .requestMatchers(
                                        "/v2/api-docs",
                                        "/v3/api-docs",
                                        "/v3/api-docs/**",
                                        "/swagger-resourses",
                                        "/swagger-resourses/**",
                                        "/configuration/ui",
                                        "/configuration/security",
                                        "/webjars/**",
                                        "/swagger-ui/**",
                                        "/swagger-ui.html",
                                        "/api/v1/auth/register",
                                        "/api/v1/auth/authenticate",
                                        "/api/v1/auth/token",
                                        "/favicon.ico",
                                        ConfirmationUrls.CONFIRMATION_URL + "/**"
                                ).permitAll()
                                .requestMatchers("/api/v1/**").hasAnyRole("USER", "ADMIN")
                                .anyRequest().authenticated()
                )
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .httpBasic(withDefaults());
        return http.build();
    }
}
