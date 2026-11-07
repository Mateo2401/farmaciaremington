package com.uniremingto.inventario.model.dao;

import com.uniremingto.inventario.model.vo.VentaVO;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface VentaDAO {

    VentaVO save(VentaVO venta);

    VentaVO update(VentaVO venta);

    boolean deleteById(Integer idVenta);

    Optional<VentaVO> findById(Integer idVenta);

    List<VentaVO> findAll();

    List<VentaVO> findByUsuario(Integer idUsuario);

    List<VentaVO> findByEstado(String estado);

    List<VentaVO> findByMetodoPago(String metodoPago);

    List<VentaVO> findByFechaVentaBetween(LocalDateTime fechaInicio, LocalDateTime fechaFin);
}
