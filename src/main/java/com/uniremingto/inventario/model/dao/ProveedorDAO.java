package com.uniremingto.inventario.model.dao;

import com.uniremingto.inventario.model.vo.ProveedorVO;

import java.util.List;
import java.util.Optional;

public interface ProveedorDAO {

    ProveedorVO save(ProveedorVO proveedor);

    ProveedorVO update(ProveedorVO proveedor);

    boolean deleteById(Integer idProveedor);

    Optional<ProveedorVO> findById(Integer idProveedor);

    List<ProveedorVO> findAll();

    List<ProveedorVO> findByEstado(Boolean estado);

    List<ProveedorVO> findByNombreContaining(String nombre);
}
