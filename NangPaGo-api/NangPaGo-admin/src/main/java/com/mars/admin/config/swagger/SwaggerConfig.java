package com.mars.admin.config.swagger;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.info.BuildProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@RequiredArgsConstructor
@Configuration
public class SwaggerConfig {

    private final BuildProperties buildProperties;

    @Bean
    public OpenAPI openAPI() {
        SecurityScheme securityScheme = getJwtSecurityScheme();
        SecurityRequirement securityRequirement = getSecurityRequirementForBearer();

        return new OpenAPI()
            .info(new Info()
                .title("NangPaGo Admin API")
                .description("NangPaGo Admin API Documentation")
                .version(buildProperties.getVersion()))
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
