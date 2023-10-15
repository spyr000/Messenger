package com.spyro.messenger.swagger.config;


import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Configuration
public class OpenApiConfig {
    @Bean
    public GroupedOpenApi authenticationApi() {
        return GroupedOpenApi.builder()
                .group("Authentication")
                .pathsToMatch("/api/v1/auth/**")
                .build();
    }

    @Bean
    public GroupedOpenApi usersApi() {
        return GroupedOpenApi.builder().group("Users")
                .pathsToMatch("/api/v1/users/**")
                .build();
    }

    @Bean
    public GroupedOpenApi friendsApi() {
        return GroupedOpenApi.builder()
                .group("Friends")
                .pathsToMatch("/api/v1/friends/**")
                .build();
    }

    @Bean
    public GroupedOpenApi messagingApi() {
        return GroupedOpenApi.builder()
                .group("Messaging")
                .pathsToMatch("/api/v1/messages/**")
                .build();
    }

    @Bean
    public OpenAPI customOpenApi(@Value("${openapi.info.description}") String appDescription,
                                 @Value("${openapi.info.version}") String appVersion,
                                 @Value("${openapi.info.title}") String title,
                                 @Value("${openapi.info.licence.name}") String licenseName,
                                 @Value("${openapi.info.licence.url}") String licenseUrl,
                                 @Value("${openapi.info.contact.name}") String contactName,
                                 @Value("${openapi.info.contact.email}") String contactEmail,
                                 @Value("#{${openapi.servers}}") Map<String,String> serversMap
    ) {
        List<Server> servers = new ArrayList<>();
        for (String key: serversMap.keySet()) {
            servers.add(new Server().url(key).description(serversMap.get(key)));
        }
        return new OpenAPI()
                .info(
                        new Info()
                                .title(title)
                                .version(appVersion)
                                .description(appDescription)
                                .license(
                                        new License()
                                                .name(licenseName)
                                                .url(licenseUrl))
                                .contact(
                                        new Contact()
                                                .name(contactName)
                                                .email(contactEmail)))
                .servers(servers);
    }
}