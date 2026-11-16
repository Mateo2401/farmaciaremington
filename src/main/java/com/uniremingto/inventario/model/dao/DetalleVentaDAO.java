package com.uniremingto.inventario.model.dao;

import com.uniremingto.inventario.model.vo.DetalleVentaVO;

import java.util.List;
import java.util.Optional;

/** Contrato de operaciones de acceso a los detalles de venta. */
public interface DetalleVentaDAO {

    /** Guarda un detalle de venta nuevo. */
    DetalleVentaVO save(DetalleVentaVO detalleVenta);

    /** Actualiza un detalle de venta existente. */
    DetalleVentaVO update(DetalleVentaVO detalleVenta);

    /** Elimina un detalle por su identificador. */
    boolean deleteById(Integer idDetalle);

    /** Busca un detalle por su identificador. */
    Optional<DetalleVentaVO> findById(Integer idDetalle);

    /** Devuelve todos los detalles de venta. */
    List<DetalleVentaVO> findAll();

    /** Filtra los detalles asociados a una venta. */
    List<DetalleVentaVO> findByVenta(Integer idVenta);

    /** Filtra los detalles asociados a un producto. */
    List<DetalleVentaVO> findByProducto(Integer idProducto);
}
