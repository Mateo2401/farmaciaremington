package com.uniremingto.inventario.model.dao;

import com.uniremingto.inventario.model.vo.MovimientoInventarioVO;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/** Implementación temporal de {@link MovimientoInventarioDAO} basada en memoria. */
@Repository
public class MovimientoInventarioDAOImpl implements MovimientoInventarioDAO {

    private final Map<Integer, MovimientoInventarioVO> store = new ConcurrentHashMap<>();
    private final AtomicInteger sequence = new AtomicInteger(1);

    @Override
    public MovimientoInventarioVO save(MovimientoInventarioVO movimiento) {
        if (movimiento.getIdMovimiento() == null) {
            movimiento.setIdMovimiento(sequence.getAndIncrement());
        }
        store.put(movimiento.getIdMovimiento(), movimiento);
        return movimiento;
    }

    @Override
    public MovimientoInventarioVO update(MovimientoInventarioVO movimiento) {
        if (movimiento.getIdMovimiento() == null) {
            throw new IllegalArgumentException("El id del movimiento es obligatorio");
        }
        store.put(movimiento.getIdMovimiento(), movimiento);
        return movimiento;
    }

    @Override
    public boolean deleteById(Integer idMovimiento) {
        return store.remove(idMovimiento) != null;
    }

    @Override
    public Optional<MovimientoInventarioVO> findById(Integer idMovimiento) {
        return Optional.ofNullable(store.get(idMovimiento));
    }

    @Override
    public List<MovimientoInventarioVO> findAll() {
        return store.values().stream().collect(Collectors.toList());
    }

    @Override
    public List<MovimientoInventarioVO> findByProducto(Integer idProducto) {
        return store.values().stream()
                .filter(m -> m.getIdProducto() != null && m.getIdProducto().equals(idProducto))
                .collect(Collectors.toList());
    }

    @Override
    public List<MovimientoInventarioVO> findByUsuario(Integer idUsuario) {
        return store.values().stream()
                .filter(m -> m.getIdUsuario() != null && m.getIdUsuario().equals(idUsuario))
                .collect(Collectors.toList());
    }

    @Override
    public List<MovimientoInventarioVO> findByTipo(String tipo) {
        return store.values().stream()
                .filter(m -> m.getTipo() != null && m.getTipo().equalsIgnoreCase(tipo))
                .collect(Collectors.toList());
    }

    @Override
    public List<MovimientoInventarioVO> findByFechaBetween(LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        return store.values().stream()
                .filter(m -> m.getFecha() != null && !m.getFecha().isBefore(fechaInicio) && !m.getFecha().isAfter(fechaFin))
                .collect(Collectors.toList());
    }
}
