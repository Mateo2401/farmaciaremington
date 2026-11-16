package com.uniremingto.inventario.model.dao;

import com.uniremingto.inventario.model.vo.MovimientoInventarioVO;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/** Contrato de operaciones de acceso al historial de inventario. */
public interface MovimientoInventarioDAO {

    /** Guarda un movimiento nuevo. */
    MovimientoInventarioVO save(MovimientoInventarioVO movimiento);

    /** Actualiza un movimiento existente. */
    MovimientoInventarioVO update(MovimientoInventarioVO movimiento);

    /** Elimina un movimiento por su identificador. */
    boolean deleteById(Integer idMovimiento);

    /** Busca un movimiento por su identificador. */
    Optional<MovimientoInventarioVO> findById(Integer idMovimiento);

    /** Devuelve todos los movimientos. */
    List<MovimientoInventarioVO> findAll();

    /** Filtra movimientos asociados a un producto. */
    List<MovimientoInventarioVO> findByProducto(Integer idProducto);

    /** Filtra movimientos registrados por un usuario. */
    List<MovimientoInventarioVO> findByUsuario(Integer idUsuario);

    /** Filtra movimientos por tipo. */
    List<MovimientoInventarioVO> findByTipo(String tipo);

    /** Filtra movimientos dentro del intervalo de fechas, incluidos sus extremos. */
    List<MovimientoInventarioVO> findByFechaBetween(LocalDateTime fechaInicio, LocalDateTime fechaFin);
}
