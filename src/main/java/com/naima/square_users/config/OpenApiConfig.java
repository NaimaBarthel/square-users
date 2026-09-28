package com.naima.square_users.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI squareUsersOpenAPI(){
        return new OpenAPI()
                .info(new Info()
                .title("Square Users API")
                .description("API REST pour la gestion des comptes utilisateurs et la validation d'existence inter-services")
                .version("1.0.0"));
    }
}
