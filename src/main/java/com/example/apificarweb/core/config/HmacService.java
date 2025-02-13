package com.example.apificarweb.core.config;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Service
public class HmacService {

    @Value("${api.secret}")
    private String secretKey;

    private Mac hmacSha256;

    @PostConstruct
    public void init() throws Exception {
        SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        hmacSha256 = Mac.getInstance("HmacSHA256");
        hmacSha256.init(secretKeySpec);
    }

    public synchronized String generateSignature(String apiKey) {
        byte[] hash = hmacSha256.doFinal(apiKey.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(hash);
    }
}
