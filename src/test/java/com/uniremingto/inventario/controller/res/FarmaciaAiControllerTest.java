package com.uniremingto.inventario.controller.res;

import com.uniremingto.inventario.config.SecurityConfig;
import com.uniremingto.inventario.service.GeminiApiException;
import com.uniremingto.inventario.service.GeminiService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FarmaciaAiRestController.class)
@Import(SecurityConfig.class)
class FarmaciaAiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private GeminiService geminiService;

    @Test
    void preguntaValidaDevuelveRespuestaDeGemini() throws Exception {
        when(geminiService.preguntar("¿Qué producto está disponible?")).thenReturn("La respuesta es ...");

        mockMvc.perform(post("/api/farmacia-ai/chat")
                        .with(user("vendedor").roles("VENDEDOR"))
                        .with(csrf())
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {"pregunta":"¿Qué producto está disponible?"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.respuesta").value("La respuesta es ..."));

        verify(geminiService).preguntar("¿Qué producto está disponible?");
    }

    @Test
    void preguntaVaciaDevuelveBadRequestSinLlamarAlServicio() throws Exception {
        mockMvc.perform(post("/api/farmacia-ai/chat")
                        .with(user("vendedor").roles("VENDEDOR"))
                        .with(csrf())
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {"pregunta":"  "}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("La pregunta no puede estar vacía."));

        verifyNoInteractions(geminiService);
    }

    @Test
    void preguntaQueSuperaMilCaracteresDevuelveBadRequest() throws Exception {
        String pregunta = "a".repeat(1001);

        mockMvc.perform(post("/api/farmacia-ai/chat")
                        .with(user("vendedor").roles("VENDEDOR"))
                        .with(csrf())
                        .contentType(APPLICATION_JSON)
                        .content("{\"pregunta\":\"" + pregunta + "\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("La pregunta no puede superar 1000 caracteres."));

        verifyNoInteractions(geminiService);
    }

    @Test
    void errorGeminiDevuelveErrorGenericoSinDetallesInternos() throws Exception {
        when(geminiService.preguntar("Prueba"))
                .thenThrow(new GeminiApiException("detalle interno con credenciales"));

        mockMvc.perform(post("/api/farmacia-ai/chat")
                        .with(user("vendedor").roles("VENDEDOR"))
                        .with(csrf())
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {"pregunta":"Prueba"}
                                """))
                .andExpect(status().isBadGateway())
                .andExpect(jsonPath("$.error")
                        .value("No fue posible obtener una respuesta de Farmacia AI."))
                .andExpect(jsonPath("$.error").value(org.hamcrest.Matchers.not(
                        org.hamcrest.Matchers.containsString("credenciales"))));
    }

    @Test
    void usuarioSinAutenticarEsRedirigidoAlLogin() throws Exception {
        mockMvc.perform(post("/api/farmacia-ai/chat")
                        .with(csrf())
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {"pregunta":"Prueba"}
                                """))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));

        verifyNoInteractions(geminiService);
    }
}
