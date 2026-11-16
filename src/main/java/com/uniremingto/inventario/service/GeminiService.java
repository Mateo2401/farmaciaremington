package com.uniremingto.inventario.service;

import com.uniremingto.inventario.config.GeminiProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class GeminiService {

    private static final Logger LOGGER = LoggerFactory.getLogger(GeminiService.class);
    private static final int MAX_DIAGNOSTIC_LENGTH = 300;

    private final RestClient restClient;
    private final GeminiProperties properties;

    public GeminiService(RestClient geminiRestClient, GeminiProperties properties) {
        this.restClient = geminiRestClient;
        this.properties = properties;
    }

    public String preguntar(String pregunta) {
        if (pregunta == null || pregunta.isBlank()) {
            throw new IllegalArgumentException("La pregunta no puede estar vacía.");
        }
        if (properties.apiKey() == null || properties.apiKey().isBlank()) {
            throw new IllegalStateException("Configura la variable de entorno GEMINI_API_KEY.");
        }

        InteractionResponse response;
        try {
            response = restClient.post()
                    .uri("/interactions")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("x-goog-api-key", properties.apiKey())
                    .body(new InteractionRequest(properties.model(), pregunta))
                    .retrieve()
                    .body(InteractionResponse.class);
        } catch (RestClientResponseException exception) {
            logFailure(exception, exception.getStatusCode().value(),
                    "Gemini rechazó la solicitud.");
            throw new GeminiApiException(
                    "Gemini respondió con el estado HTTP " + exception.getStatusCode().value() + ".",
                    exception);
        } catch (ResourceAccessException exception) {
            logFailure(exception, null,
                    "Error de red o tiempo de espera al comunicarse con Gemini.");
            throw new GeminiApiException(
                    "No se pudo conectar con Gemini dentro del tiempo configurado o hubo un error de red.",
                    exception);
        } catch (RestClientException exception) {
            logFailure(exception, null, "Error al procesar la respuesta de Gemini.");
            throw new GeminiApiException("No se pudo interpretar la respuesta de Gemini.", exception);
        }

        validateInteractionStatus(response);
        String respuesta = extractText(response);
        if (respuesta.isBlank()) {
            GeminiApiException exception =
                    new GeminiApiException("Gemini devolvió una respuesta sin contenido de texto.");
            logFailure(exception, null, exception.getMessage());
            throw exception;
        }
        return respuesta;
    }

    private void validateInteractionStatus(InteractionResponse response) {
        if (response == null || response.status() == null || response.status().isBlank()) {
            GeminiApiException exception = new GeminiApiException(
                    "Gemini devolvió una respuesta sin estado de interacción.");
            logFailure(exception, null, exception.getMessage());
            throw exception;
        }

        String status = response.status().toLowerCase(java.util.Locale.ROOT);
        if ("completed".equals(status)) {
            return;
        }

        String message = switch (status) {
            case "failed" -> "Gemini indicó que la interacción falló.";
            case "incomplete" -> "Gemini indicó que la interacción quedó incompleta.";
            default -> "Gemini no terminó la interacción (estado: " + status + ").";
        };
        GeminiApiException exception = new GeminiApiException(message);
        logFailure(exception, null, message);
        throw exception;
    }

    private void logFailure(Exception exception, Integer httpStatus, String diagnostic) {
        LOGGER.warn("Gemini request failed: exceptionType={}, httpStatus={}, message={}",
                exception.getClass().getSimpleName(),
                httpStatus == null ? "none" : httpStatus,
                sanitizeDiagnostic(diagnostic));
    }

    private String sanitizeDiagnostic(String diagnostic) {
        String sanitized = diagnostic == null || diagnostic.isBlank()
                ? "No se recibió detalle del error."
                : diagnostic;
        if (properties.apiKey() != null && !properties.apiKey().isBlank()) {
            sanitized = sanitized.replace(properties.apiKey(), "[REDACTED]");
        }
        sanitized = sanitized
                .replaceAll("(?i)(x-goog-api-key|authorization|api[_-]?key|token|secret|password)"
                        + "\\s*[\"']?\\s*[:=]\\s*[\"']?[^\\s,\"'}]+",
                        "$1=[REDACTED]")
                .replaceAll("(?i)Bearer\\s+[^\\s,\"'}]+", "Bearer [REDACTED]")
                .replaceAll("AIza[0-9A-Za-z_-]{20,}", "[REDACTED]")
                .replaceAll("[\\r\\n\\t]+", " ")
                .replaceAll("\\p{Cntrl}", " ");
        if (sanitized.length() > MAX_DIAGNOSTIC_LENGTH) {
            sanitized = sanitized.substring(0, MAX_DIAGNOSTIC_LENGTH) + "...";
        }
        return sanitized;
    }

    private record InteractionRequest(String model, String input) {
    }

    private record InteractionResponse(String status, List<OutputStep> steps) {
    }

    private record OutputStep(String type, List<OutputContent> content) {
    }

    private record OutputContent(String type, String text) {
    }

    private static String extractText(InteractionResponse response) {
        if (response == null || response.steps() == null) {
            return "";
        }
        return response.steps().stream()
                .filter(Objects::nonNull)
                .filter(step -> "model_output".equals(step.type()) && step.content() != null)
                .flatMap(step -> step.content().stream())
                .filter(Objects::nonNull)
                .filter(content -> "text".equals(content.type()) && content.text() != null)
                .map(OutputContent::text)
                .collect(Collectors.joining());
    }
}
