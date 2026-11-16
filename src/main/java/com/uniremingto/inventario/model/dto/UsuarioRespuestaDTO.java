package com.uniremingto.inventario.model.dto;

import com.uniremingto.inventario.model.vo.UsuarioVO;

import java.time.LocalDateTime;

/** Representación pública de un usuario; deliberadamente no contiene contraseña ni hash. */
public record UsuarioRespuestaDTO(
        Integer idUsuario,
        String nombre,
        String correo,
        String rol,
        Boolean estado,
        LocalDateTime fechaRegistro) {

    /** Crea la respuesta pública a partir del modelo de usuario persistido. */
    public static UsuarioRespuestaDTO desde(UsuarioVO usuario) {
        return new UsuarioRespuestaDTO(usuario.getIdUsuario(), usuario.getNombre(), usuario.getCorreo(),
                usuario.getRol(), usuario.getEstado(), usuario.getFechaRegistro());
    }
}
