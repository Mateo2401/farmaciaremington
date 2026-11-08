package com.uniremingto.inventario.model.dao;

import com.uniremingto.inventario.model.vo.UsuarioVO;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/** Implementación temporal de {@link UsuarioDAO} basada en memoria. */
@Repository
public class UsuarioDAOImpl implements UsuarioDAO {

    private final Map<Integer, UsuarioVO> store = new ConcurrentHashMap<>();
    private final AtomicInteger sequence = new AtomicInteger(1);

    @Override
    public UsuarioVO save(UsuarioVO usuario) {
        if (usuario.getIdUsuario() == null) {
            usuario.setIdUsuario(sequence.getAndIncrement());
        }
        store.put(usuario.getIdUsuario(), usuario);
        return usuario;
    }

    @Override
    public UsuarioVO update(UsuarioVO usuario) {
        if (usuario.getIdUsuario() == null) {
            throw new IllegalArgumentException("El id del usuario es obligatorio");
        }
        store.put(usuario.getIdUsuario(), usuario);
        return usuario;
    }

    @Override
    public boolean deleteById(Integer idUsuario) {
        return store.remove(idUsuario) != null;
    }

    @Override
    public Optional<UsuarioVO> findById(Integer idUsuario) {
        return Optional.ofNullable(store.get(idUsuario));
    }

    @Override
    public List<UsuarioVO> findAll() {
        return store.values().stream().collect(Collectors.toList());
    }

    @Override
    public Optional<UsuarioVO> findByCorreo(String correo) {
        return store.values().stream()
                .filter(u -> u.getCorreo() != null && u.getCorreo().equalsIgnoreCase(correo))
                .findFirst();
    }

    @Override
    public List<UsuarioVO> findByRol(String rol) {
        return store.values().stream()
                .filter(u -> u.getRol() != null && u.getRol().equalsIgnoreCase(rol))
                .collect(Collectors.toList());
    }

    @Override
    public List<UsuarioVO> findByEstado(Boolean estado) {
        return store.values().stream()
                .filter(u -> u.getEstado() != null && u.getEstado().equals(estado))
                .collect(Collectors.toList());
    }
}
