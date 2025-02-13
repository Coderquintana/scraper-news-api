package com.example.apificarweb.core.controllers;

import com.example.apificarweb.core.models.Noticia;
import com.example.apificarweb.core.services.ApiKeyService;
import com.example.apificarweb.core.services.WebScraperService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api-scraper")
@CrossOrigin(origins = "*")
public class NoticiaController {

    private final WebScraperService scraperService;
    private final ApiKeyService apiKeyService;

    public NoticiaController(WebScraperService scraperService, ApiKeyService apiKeyService) {
        this.scraperService = scraperService;
        this.apiKeyService = apiKeyService;
    }

    @GetMapping("/consulta")
    public ResponseEntity<?> buscarNoticias(
            @RequestParam(value = "q", required = false) String q,
            @RequestParam(value = "f", required = false, defaultValue = "false") boolean incluirImagen,
            @RequestHeader(value = "X-API-KEY", required = false) String receivedApiKey,
            @RequestHeader(value = "X-API-SIGNATURE", required = false) String receivedSignature) {

        // ✅ Validar API Key y su firma HMAC-SHA256
        if (receivedApiKey == null || receivedSignature == null ||
                !apiKeyService.isValidSignature(receivedApiKey, receivedSignature)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("{ \"codigo\": \"g103\", \"error\": \"No autorizado\" }");
        }

        if (q == null || q.trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("{ \"codigo\": \"g268\", \"error\": \"Parámetros inválidos\" }");
        }

        List<Noticia> noticias = scraperService.buscarNoticias(q, incluirImagen);

        if (noticias.isEmpty()) {
            return ResponseEntity.status(404)
                    .body("{ \"codigo\": \"g267\", \"error\": \"No se encuentran noticias para el texto: " + q + "\" }");
        }

        return ResponseEntity.ok(noticias);
    }

}
