package com.uniremingto.inventario.model.dao;

import com.uniremingto.inventario.model.vo.VentaVO;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Repository
public class VentaDAOImpl implements VentaDAO {

    private final Map<Integer, VentaVO> store = new ConcurrentHashMap<>();
    private final AtomicInteger sequence = new AtomicInteger(1);

    @Override
    public VentaVO save(VentaVO venta) {
        if (venta.getIdVenta() == null) {
            venta.setIdVenta(sequence.getAndIncrement());
        }
        store.put(venta.getIdVenta(), venta);
        return venta;
    }

    @Override
    public VentaVO update(VentaVO venta) {
        if (venta.getIdVenta() == null) {
            throw new IllegalArgumentException("El id de la venta es obligatorio");
        }
        store.put(venta.getIdVenta(), venta);
        return venta;
    }

    @Override
    public boolean deleteById(Integer idVenta) {
        return store.remove(idVenta) != null;
    }

    @Override
    public Optional<VentaVO> findById(Integer idVenta) {
        return Optional.ofNullable(store.get(idVenta));
    }

    @Override
    public List<VentaVO> findAll() {
        return store.values().stream().collect(Collectors.toList());
    }

    @Override
    public List<VentaVO> findByUsuario(Integer idUsuario) {
        return store.values().stream()
                .filter(v -> v.getIdUsuario() != null && v.getIdUsuario().equals(idUsuario))
                .collect(Collectors.toList());
    }

    @Override
    public List<VentaVO> findByEstado(String estado) {
        return store.values().stream()
                .filter(v -> v.getEstado() != null && v.getEstado().equalsIgnoreCase(estado))
                .collect(Collectors.toList());
    }

    @Override
    public List<VentaVO> findByMetodoPago(String metodoPago) {
        return store.values().stream()
                .filter(v -> v.getMetodoPago() != null && v.getMetodoPago().equalsIgnoreCase(metodoPago))
                .collect(Collectors.toList());
    }

    @Override
    public List<VentaVO> findByFechaVentaBetween(LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        return store.values().stream()
                .filter(v -> v.getFechaVenta() != null && !v.getFechaVenta().isBefore(fechaInicio) && !v.getFechaVenta().isAfter(fechaFin))
                .collect(Collectors.toList());
    }
}
