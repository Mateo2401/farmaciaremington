package com.uniremingto.inventario.service;

import com.uniremingto.inventario.config.GeminiConfiguration;
import com.uniremingto.inventario.config.GeminiProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import java.net.SocketTimeoutException;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.jsonPath;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withBadRequest;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withException;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@ExtendWith(OutputCaptureExtension.class)
class GeminiServiceTest {

    private RestClient.Builder builder;
    private MockRestServiceServer server;
    private GeminiService service;

    @BeforeEach
    void setUp() {
        builder = RestClient.builder().baseUrl(GeminiConfiguration.API_BASE_URL);
        server = MockRestServiceServer.bindTo(builder).build();
        service = new GeminiService(builder.build(), new GeminiProperties(
                "test-api-key", "gemini-2.5-flash", Duration.ofSeconds(5), Duration.ofSeconds(30)));
    }

    @Test
    void enviarPreguntaUsaInteractionsApiYDevuelveTextoGenerado() {
        server.expect(requestTo(GeminiConfiguration.API_BASE_URL + "/interactions"))
                .andExpect(method(POST))
                .andExpect(header("x-goog-api-key", "test-api-key"))
                .andExpect(jsonPath("$.model").value("gemini-2.5-flash"))
                .andExpect(jsonPath("$.input").value("¿Cuál es una pregunta de prueba?"))
                .andRespond(withSuccess("""
                        {"status":"completed","steps":[{"type":"model_output","content":[
                          {"type":"text","text":"Respuesta de "}
                          ,{"type":"text","text":"varios "}
                          ,{"type":"text","text":"bloques"}
                        ]},{"type":"model_output","content":[
                          {"type":"text","text":"prueba"}
                        ]}]}
                        """, org.springframework.http.MediaType.APPLICATION_JSON));

        assertEquals("Respuesta de varios bloquesprueba",
                service.preguntar("¿Cuál es una pregunta de prueba?"));
        server.verify();
    }

    @Test
    void usaModeloPredeterminadoCuandoElModeloNoEstaConfigurado() {
        service = new GeminiService(builder.build(), new GeminiProperties(
                "test-api-key", null, Duration.ofSeconds(5), Duration.ofSeconds(30)));
        server.expect(requestTo(GeminiConfiguration.API_BASE_URL + "/interactions"))
                .andExpect(jsonPath("$.model").value(GeminiProperties.DEFAULT_MODEL))
                .andRespond(withSuccess("""
                        {"status":"completed","steps":[{"type":"model_output","content":[
                          {"type":"text","text":"Respuesta"}
                        ]}]}
                        """, org.springframework.http.MediaType.APPLICATION_JSON));

        assertEquals("Respuesta", service.preguntar("Prueba"));
        server.verify();
    }

    @Test
    void usaModeloPredeterminadoCuandoElModeloEstaVacio() {
        service = new GeminiService(builder.build(), new GeminiProperties(
                "test-api-key", "  ", Duration.ofSeconds(5), Duration.ofSeconds(30)));
        server.expect(requestTo(GeminiConfiguration.API_BASE_URL + "/interactions"))
                .andExpect(jsonPath("$.model").value(GeminiProperties.DEFAULT_MODEL))
                .andRespond(withSuccess("""
                        {"status":"completed","steps":[{"type":"model_output","content":[
                          {"type":"text","text":"Respuesta"}
                        ]}]}
                        """, org.springframework.http.MediaType.APPLICATION_JSON));

        assertEquals("Respuesta", service.preguntar("Prueba"));
        server.verify();
    }

    @Test
    void informaErrorHttpSinExponerLaClave(CapturedOutput output) {
        String apiKey = "test-api-key-secret";
        server.expect(requestTo(GeminiConfiguration.API_BASE_URL + "/interactions"))
                .andRespond(withBadRequest().body(
                        "{\"error\":{\"message\":\"Invalid request; x-goog-api-key: "
                                + apiKey + "\"}}"));

        GeminiApiException exception = assertThrows(
                GeminiApiException.class, () -> service.preguntar("Prueba"));

        assertEquals("Gemini respondió con el estado HTTP 400.", exception.getMessage());
        assertTrue(output.getAll().contains("exceptionType=BadRequest"));
        assertTrue(output.getAll().contains("httpStatus=400"));
        assertFalse(output.getAll().contains(apiKey));
        server.verify();
    }

    @Test
    void informaCuandoLaSolicitudAEstallaPorTiempoDeEspera() throws Exception {
        server.expect(requestTo(GeminiConfiguration.API_BASE_URL + "/interactions"))
                .andRespond(withException(new SocketTimeoutException("Read timed out")));

        GeminiApiException exception = assertThrows(
                GeminiApiException.class, () -> service.preguntar("Prueba"));

        assertEquals(
                "No se pudo conectar con Gemini dentro del tiempo configurado o hubo un error de red.",
                exception.getMessage());
        server.verify();
    }

    @Test
    void rechazaRespuestaSinTexto() {
        server.expect(requestTo(GeminiConfiguration.API_BASE_URL + "/interactions"))
                .andRespond(withSuccess("""
                        {"status":"completed","steps":[{"type":"model_output","content":[]}]}
                        """, org.springframework.http.MediaType.APPLICATION_JSON));

        GeminiApiException exception = assertThrows(
                GeminiApiException.class, () -> service.preguntar("Prueba"));

        assertEquals("Gemini devolvió una respuesta sin contenido de texto.", exception.getMessage());
        server.verify();
    }

    @Test
    void rechazaRespuestaSinPasos() {
        server.expect(requestTo(GeminiConfiguration.API_BASE_URL + "/interactions"))
                .andRespond(withSuccess("""
                        {"status":"completed","steps":[]}
                        """, org.springframework.http.MediaType.APPLICATION_JSON));

        GeminiApiException exception = assertThrows(
                GeminiApiException.class, () -> service.preguntar("Prueba"));

        assertEquals("Gemini devolvió una respuesta sin contenido de texto.", exception.getMessage());
        server.verify();
    }

    @Test
    void rechazaInteraccionIncompleta() {
        server.expect(requestTo(GeminiConfiguration.API_BASE_URL + "/interactions"))
                .andRespond(withSuccess("""
                        {"status":"incomplete","steps":[{"type":"model_output","content":[
                          {"type":"text","text":"Respuesta parcial"}
                        ]}]}
                        """, org.springframework.http.MediaType.APPLICATION_JSON));

        GeminiApiException exception = assertThrows(
                GeminiApiException.class, () -> service.preguntar("Prueba"));

        assertEquals("Gemini indicó que la interacción quedó incompleta.", exception.getMessage());
        server.verify();
    }

    @Test
    void rechazaInteraccionFallida() {
        server.expect(requestTo(GeminiConfiguration.API_BASE_URL + "/interactions"))
                .andRespond(withSuccess("""
                        {"status":"failed","steps":[]}
                        """, org.springframework.http.MediaType.APPLICATION_JSON));

        GeminiApiException exception = assertThrows(
                GeminiApiException.class, () -> service.preguntar("Prueba"));

        assertEquals("Gemini indicó que la interacción falló.", exception.getMessage());
        server.verify();
    }

    @Test
    void exigeApiKeyAntesDeEnviarLaSolicitud() {
        GeminiService sinClave = new GeminiService(
                RestClient.builder().baseUrl(GeminiConfiguration.API_BASE_URL).build(),
                new GeminiProperties("", "gemini-2.5-flash",
                Duration.ofSeconds(5), Duration.ofSeconds(30)));

        IllegalStateException exception = assertThrows(
                IllegalStateException.class, () -> sinClave.preguntar("Prueba"));

        assertEquals("Configura la variable de entorno GEMINI_API_KEY.", exception.getMessage());
    }
}
