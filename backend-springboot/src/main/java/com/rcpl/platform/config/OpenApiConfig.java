package com.rcpl.platform.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Publishes the documented API surface at {@code /swagger-ui} with a bearer-JWT scheme,
 * so the whole {@code /api/*} contract is browsable and callable with a token.
 */
@Configuration
public class OpenApiConfig {

    private static final String BEARER = "bearer-jwt";

    @Bean
    public OpenAPI rcplOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("RCPL Partner Platform API")
                        .version("0.1.0")
                        .description("Legacy (non-agentic) REST API for the RCPL Partner Platform. "
                                + "No copilot, no agents, no LLM endpoints."))
                .addSecurityItem(new SecurityRequirement().addList(BEARER))
                .components(new Components().addSecuritySchemes(BEARER,
                        new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")));
    }
}
