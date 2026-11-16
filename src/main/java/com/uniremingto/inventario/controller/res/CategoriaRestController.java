package com.uniremingto.inventario.controller.res;

import com.uniremingto.inventario.model.dao.CategoriaDAO;
import com.uniremingto.inventario.model.vo.CategoriaVO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/** Expone operaciones REST para categorías. */
@RestController
@RequestMapping("/api/categorias")
public class CategoriaRestController {

    private final CategoriaDAO categoriaDAO;

    public CategoriaRestController(CategoriaDAO categoriaDAO) {
        this.categoriaDAO = categoriaDAO;
    }

    /** Devuelve todas las categorías. */
    @GetMapping
    public List<CategoriaVO> listarCategorias() {
        return categoriaDAO.findAll();
    }

    /** Busca una categoría y responde 404 cuando no existe. */
    @GetMapping("/{id}")
    public ResponseEntity<CategoriaVO> obtenerCategoria(@PathVariable Integer id) {
        return categoriaDAO.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /** Crea una categoría y responde 201 con el registro creado. */
    @PostMapping
    public ResponseEntity<CategoriaVO> crearCategoria(@RequestBody CategoriaVO categoria) {
        CategoriaVO guardada = categoriaDAO.save(categoria);
        return ResponseEntity.status(HttpStatus.CREATED).body(guardada);
    }

    /** Actualiza la categoría indicada por la ruta. */
    @PutMapping("/{id}")
    public ResponseEntity<CategoriaVO> actualizarCategoria(@PathVariable Integer id, @RequestBody CategoriaVO categoria) {
        categoria.setIdCategoria(id);
        CategoriaVO actualizada = categoriaDAO.update(categoria);
        return ResponseEntity.ok(actualizada);
    }

    /** Elimina la categoría o responde 404 si no existía. */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarCategoria(@PathVariable Integer id) {
        boolean eliminado = categoriaDAO.deleteById(id);
        return eliminado ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}
