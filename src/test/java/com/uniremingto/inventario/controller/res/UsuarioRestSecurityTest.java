package com.uniremingto.inventario.controller.res;

import com.uniremingto.inventario.config.SecurityConfig;
import com.uniremingto.inventario.model.vo.UsuarioVO;
import com.uniremingto.inventario.service.UsuarioService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UsuarioRestController.class)
@Import(SecurityConfig.class)
class UsuarioRestSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UsuarioService usuarioService;

    @Test
    void requestSinAutenticarEsRedirigidoAlLogin() throws Exception {
        mockMvc.perform(get("/api/usuarios"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    void vendedorNoPuedeConsultarNiCrearUsuarios() throws Exception {
        mockMvc.perform(get("/api/usuarios").with(user("vendedor").roles("VENDEDOR")))
                .andExpect(status().isForbidden());

        mockMvc.perform(post("/api/usuarios")
                        .with(user("vendedor").roles("VENDEDOR"))
                        .with(csrf())
                        .contentType("application/json")
                        .content("{\"nombre\":\"Nuevo\",\"correo\":\"nuevo@test.com\","
                                + "\"contrasena\":\"ClaveNueva123\",\"rol\":\"VENDEDOR\"}"))
                .andExpect(status().isForbidden());
    }

    @Test
    void administradorConsultaUsuariosSinRecibirElHash() throws Exception {
        UsuarioVO usuario = new UsuarioVO(4, "Usuario", "usuario@test.com", "$2a$secreto",
                "VENDEDOR", true, null);
        when(usuarioService.listar()).thenReturn(List.of(usuario));

        mockMvc.perform(get("/api/usuarios").with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].correo").value("usuario@test.com"))
                .andExpect(jsonPath("$[0].contrasena").doesNotExist())
                .andExpect(jsonPath("$[0].password").doesNotExist());
    }
}
