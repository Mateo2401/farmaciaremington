package com.uniremingto.inventario.service;

import com.uniremingto.inventario.model.dao.UsuarioDAO;
import com.uniremingto.inventario.model.vo.UsuarioVO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioDAO usuarioDAO;

    @Mock
    private org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    @InjectMocks
    private UsuarioService usuarioService;

    @Test
    void crearCodificaLaContrasenaUnaSolaVezAntesDePersistir() {
        UsuarioVO usuario = usuario("NuevaClave123", null);
        when(passwordEncoder.encode("NuevaClave123")).thenReturn("$2a$hash-generado");
        when(usuarioDAO.save(any(UsuarioVO.class))).thenAnswer(invocation -> invocation.getArgument(0));

        usuarioService.crear(usuario);

        ArgumentCaptor<UsuarioVO> captor = ArgumentCaptor.forClass(UsuarioVO.class);
        verify(usuarioDAO).save(captor.capture());
        assertEquals("$2a$hash-generado", captor.getValue().getContrasena());
        verify(passwordEncoder).encode("NuevaClave123");
    }

    @Test
    void actualizarSinContrasenaNuevaConservaElHashExistente() {
        UsuarioVO existente = usuario("$2a$hash-original", 12);
        UsuarioVO cambios = usuario(null, null);
        when(usuarioDAO.findById(12)).thenReturn(Optional.of(existente));
        when(usuarioDAO.update(any(UsuarioVO.class))).thenAnswer(invocation -> invocation.getArgument(0));

        usuarioService.actualizar(12, cambios);

        ArgumentCaptor<UsuarioVO> captor = ArgumentCaptor.forClass(UsuarioVO.class);
        verify(usuarioDAO).update(captor.capture());
        assertEquals(12, captor.getValue().getIdUsuario());
        assertEquals("$2a$hash-original", captor.getValue().getContrasena());
        verify(passwordEncoder, never()).encode(any());
    }

    @Test
    void actualizarConContrasenaNuevaLaCodificaUnaSolaVez() {
        UsuarioVO existente = usuario("$2a$hash-original", 15);
        UsuarioVO cambios = usuario("ClaveActualizada123", null);
        when(usuarioDAO.findById(15)).thenReturn(Optional.of(existente));
        when(passwordEncoder.encode("ClaveActualizada123")).thenReturn("$2a$hash-nuevo");
        when(usuarioDAO.update(any(UsuarioVO.class))).thenAnswer(invocation -> invocation.getArgument(0));

        usuarioService.actualizar(15, cambios);

        ArgumentCaptor<UsuarioVO> captor = ArgumentCaptor.forClass(UsuarioVO.class);
        verify(usuarioDAO).update(captor.capture());
        assertEquals("$2a$hash-nuevo", captor.getValue().getContrasena());
        verify(passwordEncoder).encode("ClaveActualizada123");
    }

    private static UsuarioVO usuario(String contrasena, Integer idUsuario) {
        UsuarioVO usuario = new UsuarioVO();
        usuario.setIdUsuario(idUsuario);
        usuario.setNombre("Usuario de prueba");
        usuario.setCorreo("usuario@test.com");
        usuario.setContrasena(contrasena);
        usuario.setRol("VENDEDOR");
        usuario.setEstado(true);
        return usuario;
    }
}
