package com.uniremingto.inventario.model.dao;

import com.uniremingto.inventario.model.vo.UsuarioVO;

import java.util.List;
import java.util.Optional;

/** Contrato de operaciones de acceso a usuarios. */
public interface UsuarioDAO {

    /** Guarda un usuario nuevo. */
    UsuarioVO save(UsuarioVO usuario);

    /** Actualiza un usuario existente. */
    UsuarioVO update(UsuarioVO usuario);

    /** Elimina un usuario por su identificador. */
    boolean deleteById(Integer idUsuario);

    /** Busca un usuario por su identificador. */
    Optional<UsuarioVO> findById(Integer idUsuario);

    /** Devuelve todos los usuarios. */
    List<UsuarioVO> findAll();

    /** Busca un usuario por correo electrónico. */
    Optional<UsuarioVO> findByCorreo(String correo);

    /** Filtra usuarios por rol. */
    List<UsuarioVO> findByRol(String rol);

    /** Filtra usuarios por estado activo o inactivo. */
    List<UsuarioVO> findByEstado(Boolean estado);
}
