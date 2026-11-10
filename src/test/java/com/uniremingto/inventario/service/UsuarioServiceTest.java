
package com.uniremingto.inventario.service;

import com.uniremingto.inventario.model.dao.UsuarioDAO;
import com.uniremingto.inventario.model.vo.UsuarioVO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioDAO usuarioDAO;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UsuarioService usuarioService;

    // CREACIÓN DE USUARIOS

    @Test
    void crearCodificaLaContrasenaUnaSolaVezAntesDePersistir() {
        UsuarioVO usuario = usuario("NuevaClave123", null);

        when(passwordEncoder.encode("NuevaClave123"))
                .thenReturn("$2a$hash-generado");
        when(usuarioDAO.save(any(UsuarioVO.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        usuarioService.crear(usuario);

        ArgumentCaptor<UsuarioVO> captor =
                ArgumentCaptor.forClass(UsuarioVO.class);

        verify(usuarioDAO).save(captor.capture());

        assertEquals("$2a$hash-generado",
                captor.getValue().getContrasena());

        verify(passwordEncoder).encode("NuevaClave123");
    }

    @Test
    void crearEliminaEspaciosYActivaUsuarioPorDefecto() {
        UsuarioVO usuario = usuario("Clave12345", null);
        usuario.setNombre("  Juan Pérez  ");
        usuario.setCorreo("  juan@test.com  ");

        when(passwordEncoder.encode("Clave12345"))
                .thenReturn("hash");
        when(usuarioDAO.save(any(UsuarioVO.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        UsuarioVO resultado = usuarioService.crear(usuario);

        assertEquals("Juan Pérez", resultado.getNombre());
        assertEquals("juan@test.com", resultado.getCorreo());
        assertTrue(resultado.getEstado());
    }

    @Test
    void crearRechazaNombreVacio() {
        UsuarioVO usuario = usuario("Clave12345", null);
        usuario.setNombre(" ");

        assertThrows(IllegalArgumentException.class,
                () -> usuarioService.crear(usuario));

        verify(usuarioDAO, never()).save(any());
    }

    @Test
    void crearRechazaNombreMayorA100Caracteres() {
        UsuarioVO usuario = usuario("Clave12345", null);
        usuario.setNombre("A".repeat(101));

        assertThrows(IllegalArgumentException.class,
                () -> usuarioService.crear(usuario));

        verify(usuarioDAO, never()).save(any());
    }

    @Test
    void crearRechazaCorreoInvalido() {
        UsuarioVO usuario = usuario("Clave12345", null);
        usuario.setCorreo("correo-invalido");

        assertThrows(IllegalArgumentException.class,
                () -> usuarioService.crear(usuario));

        verify(usuarioDAO, never()).save(any());
    }

    @Test
    void crearRechazaRolNoPermitido() {
        UsuarioVO usuario = usuario("Clave12345", null);
        usuario.setRol("SUPERVISOR");

        assertThrows(IllegalArgumentException.class,
                () -> usuarioService.crear(usuario));

        verify(usuarioDAO, never()).save(any());
    }

    @Test
    void crearRechazaContrasenaMenorA8Bytes() {
        UsuarioVO usuario = usuario("1234567", null);

        assertThrows(IllegalArgumentException.class,
                () -> usuarioService.crear(usuario));

        verify(usuarioDAO, never()).save(any());
        verify(passwordEncoder, never()).encode(any());
    }

    @Test
    void crearRechazaContrasenaMayorA72Bytes() {
        UsuarioVO usuario = usuario("A".repeat(73), null);

        assertThrows(IllegalArgumentException.class,
                () -> usuarioService.crear(usuario));

        verify(usuarioDAO, never()).save(any());
        verify(passwordEncoder, never()).encode(any());
    }

    // ACTUALIZACIÓN DE USUARIOS

    @Test
    void actualizarSinContrasenaNuevaConservaElHashExistente() {
        UsuarioVO existente = usuario("$2a$hash-original", 12);
        UsuarioVO cambios = usuario(null, null);

        when(usuarioDAO.findById(12)).thenReturn(Optional.of(existente));
        when(usuarioDAO.update(any(UsuarioVO.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        usuarioService.actualizar(12, cambios);

        ArgumentCaptor<UsuarioVO> captor =
                ArgumentCaptor.forClass(UsuarioVO.class);

        verify(usuarioDAO).update(captor.capture());

        assertEquals(12, captor.getValue().getIdUsuario());
        assertEquals("$2a$hash-original",
                captor.getValue().getContrasena());

        verify(passwordEncoder, never()).encode(any());
    }

    @Test
    void actualizarConContrasenaNuevaLaCodificaUnaSolaVez() {
        UsuarioVO existente = usuario("$2a$hash-original", 15);
        UsuarioVO cambios = usuario("ClaveActualizada123", null);

        when(usuarioDAO.findById(15)).thenReturn(Optional.of(existente));
        when(passwordEncoder.encode("ClaveActualizada123"))
                .thenReturn("$2a$hash-nuevo");
        when(usuarioDAO.update(any(UsuarioVO.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        usuarioService.actualizar(15, cambios);

        ArgumentCaptor<UsuarioVO> captor =
                ArgumentCaptor.forClass(UsuarioVO.class);

        verify(usuarioDAO).update(captor.capture());

        assertEquals("$2a$hash-nuevo",
                captor.getValue().getContrasena());

        verify(passwordEncoder).encode("ClaveActualizada123");
    }

    @Test
    void actualizarUsuarioInexistenteLanzaExcepcion() {
        UsuarioVO cambios = usuario(null, null);

        when(usuarioDAO.findById(99)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> usuarioService.actualizar(99, cambios));

        verify(usuarioDAO, never()).update(any());
    }

    @Test
    void actualizarConservaFechaYEstadoCuandoNoSeEnvianCambios() {
        LocalDateTime fecha = LocalDateTime.of(2026, 1, 10, 10, 30);

        UsuarioVO existente = usuario("hash-original", 20);
        existente.setFechaRegistro(fecha);
        existente.setEstado(false);

        UsuarioVO cambios = usuario(null, null);
        cambios.setEstado(null);

        when(usuarioDAO.findById(20)).thenReturn(Optional.of(existente));
        when(usuarioDAO.update(any(UsuarioVO.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        UsuarioVO resultado = usuarioService.actualizar(20, cambios);

        assertEquals(fecha, resultado.getFechaRegistro());
        assertFalse(resultado.getEstado());
        assertEquals(20, resultado.getIdUsuario());
    }

    @Test
    void actualizarRechazaRolInvalido() {
        UsuarioVO existente = usuario("hash-original", 25);
        UsuarioVO cambios = usuario(null, null);
        cambios.setRol("INVITADO");

        when(usuarioDAO.findById(25)).thenReturn(Optional.of(existente));

        assertThrows(IllegalArgumentException.class,
                () -> usuarioService.actualizar(25, cambios));

        verify(usuarioDAO, never()).update(any());
    }

    // CONSULTAS DE USUARIOS

    @Test
    void listarDevuelveLosUsuariosDelDao() {
        List<UsuarioVO> usuarios = List.of(
                usuario("hash1", 1),
                usuario("hash2", 2)
        );

        when(usuarioDAO.findAll()).thenReturn(usuarios);

        List<UsuarioVO> resultado = usuarioService.listar();

        assertEquals(2, resultado.size());
        assertEquals(usuarios, resultado);
        verify(usuarioDAO).findAll();
    }

    @Test
    void buscarPorIdDevuelveUsuarioExistente() {
        UsuarioVO esperado = usuario("hash", 10);

        when(usuarioDAO.findById(10)).thenReturn(Optional.of(esperado));

        Optional<UsuarioVO> resultado = usuarioService.buscarPorId(10);

        assertTrue(resultado.isPresent());
        assertEquals(10, resultado.get().getIdUsuario());
    }

    @Test
    void buscarPorIdDevuelveVacioCuandoNoExiste() {
        when(usuarioDAO.findById(50)).thenReturn(Optional.empty());

        Optional<UsuarioVO> resultado = usuarioService.buscarPorId(50);

        assertTrue(resultado.isEmpty());
    }

    // ELIMINACIÓN DE USUARIOS

    @Test
    void eliminarDevuelveVerdaderoCuandoElDaoEliminaElUsuario() {
        when(usuarioDAO.deleteById(10)).thenReturn(true);

        boolean resultado = usuarioService.eliminar(10);

        assertTrue(resultado);
        verify(usuarioDAO).deleteById(10);
    }

    @Test
    void eliminarDevuelveFalsoCuandoElUsuarioNoExiste() {
        when(usuarioDAO.deleteById(99)).thenReturn(false);

        boolean resultado = usuarioService.eliminar(99);

        assertFalse(resultado);
        verify(usuarioDAO).deleteById(99);
    }

    // MÉTODO AUXILIAR

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
