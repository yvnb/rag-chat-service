package com.spring.ragchatservice.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    private static final String API_KEY_NAME = "X-API-Key";

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info().title("RAG Chat Service API")
                        .description("APIs for Chat Session Management")
                        .version("v1"))
                .addSecurityItem(new SecurityRequirement().addList(API_KEY_NAME))
                .components(new Components()
                        .addSecuritySchemes(API_KEY_NAME,
                                new SecurityScheme()
                                        .name(API_KEY_NAME)
                                        .type(SecurityScheme.Type.APIKEY)
                                        .in(SecurityScheme.In.HEADER)
                        ));
    }
}
