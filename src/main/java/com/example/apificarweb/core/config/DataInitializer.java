package com.example.apificarweb.core.config;

import com.example.apificarweb.core.services.ApiKeyService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initApiKeys(ApiKeyService apiKeyService) {
        return args -> {
            // Si no hay API Keys, crear una de prueba
            if (!apiKeyService.isValidApiKey("123456789ABCDEF")) {
                apiKeyService.agregarApiKey("123456789ABCDEF");
            }
        };
    }
}
