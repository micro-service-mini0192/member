package com.project.project.config.util;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@OpenAPIDefinition(
        info = @Info(title = "Title",
                description = "description",
                version = "v1"))
@Configuration
public class SwaggerConfig {
    @Bean
    public GroupedOpenApi group1() {
        String[] paths = {"/**"};

        return GroupedOpenApi.builder()
                .group("Member API")
                .pathsToMatch(paths)
                .build();
    }
}
