package com.uniremingto.inventario.model.dao;

import com.uniremingto.inventario.model.vo.ProveedorVO;

import java.util.List;
import java.util.Optional;

/** Contrato de operaciones de acceso a proveedores. */
public interface ProveedorDAO {

    /** Guarda un proveedor nuevo. */
    ProveedorVO save(ProveedorVO proveedor);

    /** Actualiza un proveedor existente. */
    ProveedorVO update(ProveedorVO proveedor);

    /** Elimina un proveedor por su identificador. */
    boolean deleteById(Integer idProveedor);

    /** Busca un proveedor por su identificador. */
    Optional<ProveedorVO> findById(Integer idProveedor);

    /** Devuelve todos los proveedores. */
    List<ProveedorVO> findAll();

    /** Filtra proveedores por estado activo o inactivo. */
    List<ProveedorVO> findByEstado(Boolean estado);

    /** Busca proveedores cuyo nombre contiene el texto recibido. */
    List<ProveedorVO> findByNombreContaining(String nombre);
}
