package com.uniremingto.inventario.service;

import com.uniremingto.inventario.model.dao.UsuarioDAO;
import com.uniremingto.inventario.model.vo.UsuarioVO;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/** Centraliza las reglas de negocio y protección de credenciales de los usuarios. */
@Service
public class UsuarioService {

    private static final Set<String> ROLES_VALIDOS = Set.of("ADMINISTRADOR", "VENDEDOR");
    private final UsuarioDAO usuarioDAO;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioDAO usuarioDAO, PasswordEncoder passwordEncoder) {
        this.usuarioDAO = usuarioDAO;
        this.passwordEncoder = passwordEncoder;
    }

    /** Devuelve los usuarios para las operaciones internas de la aplicación. */
    public List<UsuarioVO> listar() {
        return usuarioDAO.findAll();
    }

    /** Busca un usuario por su identificador. */
    public Optional<UsuarioVO> buscarPorId(Integer idUsuario) {
        return usuarioDAO.findById(idUsuario);
    }

    /** Valida y codifica una contraseña en texto claro antes de persistir al usuario. */
    public UsuarioVO crear(UsuarioVO usuario) {
        validarDatos(usuario, true);
        usuario.setNombre(usuario.getNombre().trim());
        usuario.setCorreo(usuario.getCorreo().trim());
        usuario.setContrasena(passwordEncoder.encode(usuario.getContrasena()));
        if (usuario.getEstado() == null) {
            usuario.setEstado(true);
        }
        return usuarioDAO.save(usuario);
    }

    /**
     * Actualiza un usuario; si no se recibe una contraseña nueva conserva el hash existente.
     */
    public UsuarioVO actualizar(Integer idUsuario, UsuarioVO cambios) {
        UsuarioVO existente = usuarioDAO.findById(idUsuario)
                .orElseThrow(() -> new IllegalArgumentException("No existe el usuario con id " + idUsuario));
        validarDatos(cambios, cambios.getContrasena() != null && !cambios.getContrasena().isBlank());

        cambios.setIdUsuario(idUsuario);
        cambios.setNombre(cambios.getNombre().trim());
        cambios.setCorreo(cambios.getCorreo().trim());
        cambios.setFechaRegistro(existente.getFechaRegistro());
        if (cambios.getContrasena() == null || cambios.getContrasena().isBlank()) {
            cambios.setContrasena(existente.getContrasena());
        } else {
            cambios.setContrasena(passwordEncoder.encode(cambios.getContrasena()));
        }
        if (cambios.getEstado() == null) {
            cambios.setEstado(existente.getEstado());
        }
        return usuarioDAO.update(cambios);
    }

    /** Elimina un usuario y devuelve si existía. */
    public boolean eliminar(Integer idUsuario) {
        return usuarioDAO.deleteById(idUsuario);
    }

    private void validarDatos(UsuarioVO usuario, boolean requiereContrasena) {
        if (usuario.getNombre() == null || usuario.getNombre().isBlank() || usuario.getNombre().trim().length() > 100) {
            throw new IllegalArgumentException("El nombre es obligatorio y no puede superar 100 caracteres.");
        }
        if (usuario.getCorreo() == null || usuario.getCorreo().isBlank()
                || !usuario.getCorreo().contains("@") || usuario.getCorreo().trim().length() > 100) {
            throw new IllegalArgumentException("Ingresa un correo válido de máximo 100 caracteres.");
        }
        if (usuario.getRol() == null || !ROLES_VALIDOS.contains(usuario.getRol())) {
            throw new IllegalArgumentException("El rol debe ser ADMINISTRADOR o VENDEDOR.");
        }
        if (requiereContrasena) {
            String contrasena = usuario.getContrasena();
            int longitudEnBytes = contrasena == null ? 0 : contrasena.getBytes(StandardCharsets.UTF_8).length;
            if (longitudEnBytes < 8 || longitudEnBytes > 72) {
                throw new IllegalArgumentException("La contraseña debe tener entre 8 y 72 bytes.");
            }
        }
    }
}
