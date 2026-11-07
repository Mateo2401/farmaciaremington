package com.uniremingto.inventario.controller.res;

import com.uniremingto.inventario.model.dao.ProveedorDAO;
import com.uniremingto.inventario.model.vo.ProveedorVO;
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
@RequestMapping("/api/proveedores")
public class ProveedorRestController {

    private final ProveedorDAO proveedorDAO;

    public ProveedorRestController(ProveedorDAO proveedorDAO) {
        this.proveedorDAO = proveedorDAO;
    }

    @GetMapping
    public List<ProveedorVO> listarProveedores() {
        return proveedorDAO.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProveedorVO> obtenerProveedor(@PathVariable Integer id) {
        return proveedorDAO.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<ProveedorVO> crearProveedor(@RequestBody ProveedorVO proveedor) {
        ProveedorVO guardado = proveedorDAO.save(proveedor);
        return ResponseEntity.status(HttpStatus.CREATED).body(guardado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProveedorVO> actualizarProveedor(@PathVariable Integer id, @RequestBody ProveedorVO proveedor) {
        proveedor.setIdProveedor(id);
        ProveedorVO actualizado = proveedorDAO.update(proveedor);
        return ResponseEntity.ok(actualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarProveedor(@PathVariable Integer id) {
        boolean eliminado = proveedorDAO.deleteById(id);
        return eliminado ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}
