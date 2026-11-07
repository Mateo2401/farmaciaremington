package com.uniremingto.inventario.model.dao;

import com.uniremingto.inventario.model.vo.ProveedorVO;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Repository
public class ProveedorDAOImpl implements ProveedorDAO {

    private final Map<Integer, ProveedorVO> store = new ConcurrentHashMap<>();
    private final AtomicInteger sequence = new AtomicInteger(1);

    @Override
    public ProveedorVO save(ProveedorVO proveedor) {
        if (proveedor.getIdProveedor() == null) {
            proveedor.setIdProveedor(sequence.getAndIncrement());
        }
        store.put(proveedor.getIdProveedor(), proveedor);
        return proveedor;
    }

    @Override
    public ProveedorVO update(ProveedorVO proveedor) {
        if (proveedor.getIdProveedor() == null) {
            throw new IllegalArgumentException("El id del proveedor es obligatorio");
        }
        store.put(proveedor.getIdProveedor(), proveedor);
        return proveedor;
    }

    @Override
    public boolean deleteById(Integer idProveedor) {
        return store.remove(idProveedor) != null;
    }

    @Override
    public Optional<ProveedorVO> findById(Integer idProveedor) {
        return Optional.ofNullable(store.get(idProveedor));
    }

    @Override
    public List<ProveedorVO> findAll() {
        return store.values().stream().collect(Collectors.toList());
    }

    @Override
    public List<ProveedorVO> findByEstado(Boolean estado) {
        return store.values().stream()
                .filter(p -> p.getEstado() != null && p.getEstado().equals(estado))
                .collect(Collectors.toList());
    }

    @Override
    public List<ProveedorVO> findByNombreContaining(String nombre) {
        return store.values().stream()
                .filter(p -> p.getNombre() != null && p.getNombre().toLowerCase().contains(nombre.toLowerCase()))
                .collect(Collectors.toList());
    }
}
