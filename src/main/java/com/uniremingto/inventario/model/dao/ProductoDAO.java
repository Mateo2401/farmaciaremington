package com.uniremingto.inventario.model.dao;

import com.uniremingto.inventario.model.vo.ProductoVO;

import java.util.List;
import java.util.Optional;

public interface ProductoDAO {

    ProductoVO save(ProductoVO producto);

    ProductoVO update(ProductoVO producto);

    boolean deleteById(Integer idProducto);

    Optional<ProductoVO> findById(Integer idProducto);

    List<ProductoVO> findAll();

    Optional<ProductoVO> findByCodigo(String codigo);

    List<ProductoVO> findByNombreContaining(String nombre);

    List<ProductoVO> findByCategoria(Integer idCategoria);

    List<ProductoVO> findByProveedor(Integer idProveedor);

    List<ProductoVO> findByEstado(Boolean estado);

    List<ProductoVO> findByStockLessThan(Integer stock);

    List<ProductoVO> findLowStockProducts(Integer stockMinimo);
}
