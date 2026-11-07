package com.uniremingto.inventario.model.dao;

import com.uniremingto.inventario.model.vo.DetalleVentaVO;

import java.util.List;
import java.util.Optional;

public interface DetalleVentaDAO {

    DetalleVentaVO save(DetalleVentaVO detalleVenta);

    DetalleVentaVO update(DetalleVentaVO detalleVenta);

    boolean deleteById(Integer idDetalle);

    Optional<DetalleVentaVO> findById(Integer idDetalle);

    List<DetalleVentaVO> findAll();

    List<DetalleVentaVO> findByVenta(Integer idVenta);

    List<DetalleVentaVO> findByProducto(Integer idProducto);
}
