package com.mars.NangPaGo.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        SecurityScheme securityScheme = getJwtSecurityScheme();
        SecurityRequirement securityRequirement = getSecurityRequirementForBearer();

        return new OpenAPI()
            .info(new Info()
                .title("NangPaGo API")
                .description("NangPaGo API Documentation")
                .version("v0.2.0"))
            .addSecurityItem(securityRequirement)
            .schemaRequirement("BearerAuth", securityScheme);
    }

    private SecurityScheme getJwtSecurityScheme() {
        return new SecurityScheme()
            .type(SecurityScheme.Type.HTTP)
            .scheme("bearer")
            .bearerFormat("JWT")
            .in(SecurityScheme.In.HEADER)
            .name("Authorization");
    }

    private static SecurityRequirement getSecurityRequirementForBearer() {
        return new SecurityRequirement()
            .addList("BearerAuth");
    }
}
