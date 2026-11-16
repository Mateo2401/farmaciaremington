package com.uniremingto.inventario.model.dao;

import com.uniremingto.inventario.model.vo.CategoriaVO;

import java.util.List;
import java.util.Optional;

/** Contrato de operaciones de acceso a categorías. */
public interface CategoriaDAO {

    /** Guarda una categoría nueva. */
    CategoriaVO save(CategoriaVO categoria);

    /** Actualiza una categoría existente. */
    CategoriaVO update(CategoriaVO categoria);

    /** Elimina una categoría por su identificador. */
    boolean deleteById(Integer idCategoria);

    /** Busca una categoría por su identificador. */
    Optional<CategoriaVO> findById(Integer idCategoria);

    /** Devuelve todas las categorías. */
    List<CategoriaVO> findAll();

    /** Busca una categoría por nombre exacto. */
    Optional<CategoriaVO> findByNombre(String nombre);

    /** Busca categorías cuyo nombre contiene el texto recibido. */
    List<CategoriaVO> findByNombreContaining(String nombre);
}
