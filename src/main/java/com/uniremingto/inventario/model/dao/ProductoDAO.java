package com.uniremingto.inventario.model.dao;

import com.uniremingto.inventario.model.vo.ProductoVO;

import java.util.List;
import java.util.Optional;

/** Contrato de persistencia y consultas de productos farmacéuticos. */
public interface ProductoDAO {

    /** Inserta un producto y devuelve el objeto con su identificador generado. */
    ProductoVO save(ProductoVO producto);

    /** Actualiza los campos de un producto existente. */
    ProductoVO update(ProductoVO producto);

    /** Elimina un producto y devuelve si se afectó una fila. */
    boolean deleteById(Integer idProducto);

    /** Busca un producto por su identificador. */
    Optional<ProductoVO> findById(Integer idProducto);

    /** Devuelve todos los productos. */
    List<ProductoVO> findAll();

    /** Busca un producto por código. */
    Optional<ProductoVO> findByCodigo(String codigo);

    /** Busca productos cuyo nombre contiene el texto recibido. */
    List<ProductoVO> findByNombreContaining(String nombre);

    /** Filtra productos por categoría. */
    List<ProductoVO> findByCategoria(Integer idCategoria);

    /** Filtra productos por proveedor. */
    List<ProductoVO> findByProveedor(Integer idProveedor);

    /** Filtra productos por estado activo o inactivo. */
    List<ProductoVO> findByEstado(Boolean estado);

    /** Busca productos con stock menor al límite indicado. */
    List<ProductoVO> findByStockLessThan(Integer stock);

    /** Busca productos cuyo stock está en o debajo del mínimo indicado. */
    List<ProductoVO> findLowStockProducts(Integer stockMinimo);
}
