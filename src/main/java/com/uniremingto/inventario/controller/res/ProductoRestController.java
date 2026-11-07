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

@RestController
@RequestMapping("/api/productos")
public class ProductoRestController {

    private final ProductoDAO productoDAO;

    public ProductoRestController(ProductoDAO productoDAO) {
        this.productoDAO = productoDAO;
    }

    @GetMapping
    public List<ProductoVO> listarProductos() {
        return productoDAO.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductoVO> obtenerProducto(@PathVariable Integer id) {
        return productoDAO.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<ProductoVO> crearProducto(@RequestBody ProductoVO producto) {
        ProductoVO guardado = productoDAO.save(producto);
        return ResponseEntity.status(HttpStatus.CREATED).body(guardado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductoVO> actualizarProducto(@PathVariable Integer id, @RequestBody ProductoVO producto) {
        producto.setIdProducto(id);
        ProductoVO actualizado = productoDAO.update(producto);
        return ResponseEntity.ok(actualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarProducto(@PathVariable Integer id) {
        boolean eliminado = productoDAO.deleteById(id);
        return eliminado ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}
