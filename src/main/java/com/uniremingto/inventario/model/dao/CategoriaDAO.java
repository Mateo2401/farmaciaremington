package com.uniremingto.inventario.model.dao;

import com.uniremingto.inventario.model.vo.CategoriaVO;

import java.util.List;
import java.util.Optional;

public interface CategoriaDAO {

    CategoriaVO save(CategoriaVO categoria);

    CategoriaVO update(CategoriaVO categoria);

    boolean deleteById(Integer idCategoria);

    Optional<CategoriaVO> findById(Integer idCategoria);

    List<CategoriaVO> findAll();

    Optional<CategoriaVO> findByNombre(String nombre);

    List<CategoriaVO> findByNombreContaining(String nombre);
}
