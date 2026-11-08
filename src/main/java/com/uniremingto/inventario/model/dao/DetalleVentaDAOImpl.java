package com.uniremingto.inventario.model.dao;

import com.uniremingto.inventario.model.vo.DetalleVentaVO;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/** Implementación temporal de {@link DetalleVentaDAO} basada en memoria. */
@Repository
public class DetalleVentaDAOImpl implements DetalleVentaDAO {

    private final Map<Integer, DetalleVentaVO> store = new ConcurrentHashMap<>();
    private final AtomicInteger sequence = new AtomicInteger(1);

    @Override
    public DetalleVentaVO save(DetalleVentaVO detalleVenta) {
        if (detalleVenta.getIdDetalle() == null) {
            detalleVenta.setIdDetalle(sequence.getAndIncrement());
        }
        store.put(detalleVenta.getIdDetalle(), detalleVenta);
        return detalleVenta;
    }

    @Override
    public DetalleVentaVO update(DetalleVentaVO detalleVenta) {
        if (detalleVenta.getIdDetalle() == null) {
            throw new IllegalArgumentException("El id del detalle es obligatorio");
        }
        store.put(detalleVenta.getIdDetalle(), detalleVenta);
        return detalleVenta;
    }

    @Override
    public boolean deleteById(Integer idDetalle) {
        return store.remove(idDetalle) != null;
    }

    @Override
    public Optional<DetalleVentaVO> findById(Integer idDetalle) {
        return Optional.ofNullable(store.get(idDetalle));
    }

    @Override
    public List<DetalleVentaVO> findAll() {
        return store.values().stream().collect(Collectors.toList());
    }

    @Override
    public List<DetalleVentaVO> findByVenta(Integer idVenta) {
        return store.values().stream()
                .filter(d -> d.getIdVenta() != null && d.getIdVenta().equals(idVenta))
                .collect(Collectors.toList());
    }

    @Override
    public List<DetalleVentaVO> findByProducto(Integer idProducto) {
        return store.values().stream()
                .filter(d -> d.getIdProducto() != null && d.getIdProducto().equals(idProducto))
                .collect(Collectors.toList());
    }
}
