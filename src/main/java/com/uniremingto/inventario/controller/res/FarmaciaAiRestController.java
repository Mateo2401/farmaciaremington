package com.uniremingto.inventario.controller.res;

import com.uniremingto.inventario.model.dto.FarmaciaAiChatRequest;
import com.uniremingto.inventario.model.dto.FarmaciaAiChatResponse;
import com.uniremingto.inventario.model.dto.FarmaciaAiErrorResponse;
import com.uniremingto.inventario.service.GeminiApiException;
import com.uniremingto.inventario.service.GeminiService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/farmacia-ai")
public class FarmaciaAiRestController {

    private static final int MAX_PREGUNTA_LENGTH = 1000;

    private final GeminiService geminiService;

    public FarmaciaAiRestController(GeminiService geminiService) {
        this.geminiService = geminiService;
    }

    @PostMapping("/chat")
    public ResponseEntity<?> chat(@RequestBody FarmaciaAiChatRequest request) {
        String pregunta = request == null ? null : request.pregunta();
        if (pregunta == null || pregunta.isBlank()) {
            return ResponseEntity.badRequest()
                    .body(new FarmaciaAiErrorResponse("La pregunta no puede estar vacía."));
        }
        if (pregunta.length() > MAX_PREGUNTA_LENGTH) {
            return ResponseEntity.badRequest()
                    .body(new FarmaciaAiErrorResponse("La pregunta no puede superar 1000 caracteres."));
        }

        try {
            return ResponseEntity.ok(new FarmaciaAiChatResponse(geminiService.preguntar(pregunta)));
        } catch (GeminiApiException exception) {
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                    .body(new FarmaciaAiErrorResponse("No fue posible obtener una respuesta de Farmacia AI."));
        } catch (IllegalStateException exception) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(new FarmaciaAiErrorResponse("Farmacia AI no está configurada o disponible."));
        }
    }
}
