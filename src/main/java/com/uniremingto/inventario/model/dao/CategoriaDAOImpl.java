package com.uniremingto.inventario.model.dao;

import com.uniremingto.inventario.model.vo.CategoriaVO;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/** Implementación temporal de {@link CategoriaDAO} basada en memoria. */
@Repository
public class CategoriaDAOImpl implements CategoriaDAO {

    private final Map<Integer, CategoriaVO> store = new ConcurrentHashMap<>();
    private final AtomicInteger sequence = new AtomicInteger(1);

    @Override
    public CategoriaVO save(CategoriaVO categoria) {
        if (categoria.getIdCategoria() == null) {
            categoria.setIdCategoria(sequence.getAndIncrement());
        }
        store.put(categoria.getIdCategoria(), categoria);
        return categoria;
    }

    @Override
    public CategoriaVO update(CategoriaVO categoria) {
        if (categoria.getIdCategoria() == null) {
            throw new IllegalArgumentException("El id de la categoría es obligatorio");
        }
        store.put(categoria.getIdCategoria(), categoria);
        return categoria;
    }

    @Override
    public boolean deleteById(Integer idCategoria) {
        return store.remove(idCategoria) != null;
    }

    @Override
    public Optional<CategoriaVO> findById(Integer idCategoria) {
        return Optional.ofNullable(store.get(idCategoria));
    }

    @Override
    public List<CategoriaVO> findAll() {
        return store.values().stream().collect(Collectors.toList());
    }

    @Override
    public Optional<CategoriaVO> findByNombre(String nombre) {
        return store.values().stream()
                .filter(c -> c.getNombre() != null && c.getNombre().equalsIgnoreCase(nombre))
                .findFirst();
    }

    @Override
    public List<CategoriaVO> findByNombreContaining(String nombre) {
        return store.values().stream()
                .filter(c -> c.getNombre() != null && c.getNombre().toLowerCase().contains(nombre.toLowerCase()))
                .collect(Collectors.toList());
    }
}
