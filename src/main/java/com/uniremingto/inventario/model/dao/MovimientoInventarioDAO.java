package com.uniremingto.inventario.model.dao;

import com.uniremingto.inventario.model.vo.MovimientoInventarioVO;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface MovimientoInventarioDAO {

    MovimientoInventarioVO save(MovimientoInventarioVO movimiento);

    MovimientoInventarioVO update(MovimientoInventarioVO movimiento);

    boolean deleteById(Integer idMovimiento);

    Optional<MovimientoInventarioVO> findById(Integer idMovimiento);

    List<MovimientoInventarioVO> findAll();

    List<MovimientoInventarioVO> findByProducto(Integer idProducto);

    List<MovimientoInventarioVO> findByUsuario(Integer idUsuario);

    List<MovimientoInventarioVO> findByTipo(String tipo);

    List<MovimientoInventarioVO> findByFechaBetween(LocalDateTime fechaInicio, LocalDateTime fechaFin);
}
