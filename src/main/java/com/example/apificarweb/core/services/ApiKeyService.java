package com.example.apificarweb.core.services;

import com.example.apificarweb.core.models.ApiKey;
import com.example.apificarweb.core.repositories.ApiKeyRepository;
import com.example.apificarweb.core.config.HmacService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ApiKeyService {
    private final ApiKeyRepository apiKeyRepository;
    private final HmacService hmacService;

    public ApiKeyService(ApiKeyRepository apiKeyRepository, HmacService hmacService) {
        this.apiKeyRepository = apiKeyRepository;
        this.hmacService = hmacService;
    }

    public boolean isValidApiKey(String apiKey) {
        Optional<ApiKey> key = apiKeyRepository.findByKeyValueAndActivoTrue(apiKey);
        return key.isPresent();
    }

    // Nuevo método para agregar API Keys con firma
    public void agregarApiKey(String key) {
        ApiKey apiKey = new ApiKey();
        apiKey.setKeyValue(key);
        apiKey.setSignature(hmacService.generateSignature(key)); // 🔥 Generar firma HMAC-SHA256
        apiKey.setActivo(true);
        apiKeyRepository.save(apiKey);
    }

    // Nuevo método para verificar la firma de una API Key
    public boolean isValidSignature(String apiKey, String receivedSignature) {
        return apiKeyRepository.findByKeyValueAndActivoTrue(apiKey)
                .map(key -> key.getSignature().equals(receivedSignature))
                .orElse(false);
    }

}
