package com.insurance.integrationhub.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI insuranceIntegrationHubOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Insurance Integration Hub API")
                        .description("보험사 금융 IT 인터페이스 통합관리 시스템 API 문서입니다.")
                        .version("v1.0.0")
                        .contact(new Contact()
                                .name("Insurance Integration Hub")
                                .email("admin@integrationhub.com"))
                        .license(new License()
                                .name("Internal Project")))
                .externalDocs(new ExternalDocumentation()
                        .description("Insurance Integration Hub API Documentation"));
    }
}