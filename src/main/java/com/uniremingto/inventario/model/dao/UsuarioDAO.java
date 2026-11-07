package com.uniremingto.inventario.model.dao;

import com.uniremingto.inventario.model.vo.UsuarioVO;

import java.util.List;
import java.util.Optional;

public interface UsuarioDAO {

    UsuarioVO save(UsuarioVO usuario);

    UsuarioVO update(UsuarioVO usuario);

    boolean deleteById(Integer idUsuario);

    Optional<UsuarioVO> findById(Integer idUsuario);

    List<UsuarioVO> findAll();

    Optional<UsuarioVO> findByCorreo(String correo);

    List<UsuarioVO> findByRol(String rol);

    List<UsuarioVO> findByEstado(Boolean estado);
}
