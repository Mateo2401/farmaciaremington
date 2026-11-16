package com.uniremingto.inventario.controller.res;

import com.uniremingto.inventario.model.dao.ProductoDAO;
import com.uniremingto.inventario.model.vo.ProductoVO;
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

/** Expone operaciones REST para consultar y administrar productos. */
@RestController
@RequestMapping("/api/productos")
public class ProductoRestController {

    private final ProductoDAO productoDAO;

    public ProductoRestController(ProductoDAO productoDAO) {
        this.productoDAO = productoDAO;
    }

    /** Devuelve los productos persistidos. */
    @GetMapping
    public List<ProductoVO> listarProductos() {
        return productoDAO.findAll();
    }

    /** Busca un producto y responde 404 cuando no existe. */
    @GetMapping("/{id}")
    public ResponseEntity<ProductoVO> obtenerProducto(@PathVariable Integer id) {
        return productoDAO.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /** Inserta un producto y responde 201 con el registro creado. */
    @PostMapping
    public ResponseEntity<ProductoVO> crearProducto(@RequestBody ProductoVO producto) {
        ProductoVO guardado = productoDAO.save(producto);
        return ResponseEntity.status(HttpStatus.CREATED).body(guardado);
    }

    /** Actualiza el ID del producto desde la ruta y persiste sus cambios. */
    @PutMapping("/{id}")
    public ResponseEntity<ProductoVO> actualizarProducto(@PathVariable Integer id, @RequestBody ProductoVO producto) {
        producto.setIdProducto(id);
        ProductoVO actualizado = productoDAO.update(producto);
        return ResponseEntity.ok(actualizado);
    }

    /** Elimina el producto o responde 404 si no existía. */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarProducto(@PathVariable Integer id) {
        boolean eliminado = productoDAO.deleteById(id);
        return eliminado ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}
