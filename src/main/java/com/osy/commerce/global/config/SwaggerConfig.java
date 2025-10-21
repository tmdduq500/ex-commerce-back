package com.osy.commerce.global.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Ex-Commerce API")
                        .description("Toy Project - 커머스 백엔드 API 명세서")
                        .version("v1.0"))
                .servers(List.of(new Server().url("http://localhost:8000")));
    }


    @Bean
    public GroupedOpenApi v1GroupedOpenApi() {
        return GroupedOpenApi.builder()
                .group("v1")
                .pathsToMatch("/api/v1/**")
                .build();
    }
}
