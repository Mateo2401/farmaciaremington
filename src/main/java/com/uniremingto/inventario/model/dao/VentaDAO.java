package com.uniremingto.inventario.model.dao;

import com.uniremingto.inventario.model.vo.VentaVO;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/** Contrato de operaciones de acceso a ventas. */
public interface VentaDAO {

    /** Guarda una venta nueva. */
    VentaVO save(VentaVO venta);

    /** Actualiza una venta existente. */
    VentaVO update(VentaVO venta);

    /** Elimina una venta por su identificador. */
    boolean deleteById(Integer idVenta);

    /** Busca una venta por su identificador. */
    Optional<VentaVO> findById(Integer idVenta);

    /** Devuelve todas las ventas. */
    List<VentaVO> findAll();

    /** Filtra ventas por el usuario que las registró. */
    List<VentaVO> findByUsuario(Integer idUsuario);

    /** Filtra ventas por estado. */
    List<VentaVO> findByEstado(String estado);

    /** Filtra ventas por método de pago. */
    List<VentaVO> findByMetodoPago(String metodoPago);

    /** Filtra ventas dentro del intervalo de fechas, incluidos sus extremos. */
    List<VentaVO> findByFechaVentaBetween(LocalDateTime fechaInicio, LocalDateTime fechaFin);
}
