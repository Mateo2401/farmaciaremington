package com.uniremingto.inventario.controller.res;

import com.uniremingto.inventario.model.dao.VentaDAO;
import com.uniremingto.inventario.model.vo.VentaVO;
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

/** Expone operaciones REST para ventas. */
@RestController
@RequestMapping("/api/ventas")
public class VentaRestController {

    private final VentaDAO ventaDAO;

    public VentaRestController(VentaDAO ventaDAO) {
        this.ventaDAO = ventaDAO;
    }

    /** Devuelve todas las ventas. */
    @GetMapping
    public List<VentaVO> listarVentas() {
        return ventaDAO.findAll();
    }

    /** Busca una venta y responde 404 cuando no existe. */
    @GetMapping("/{id}")
    public ResponseEntity<VentaVO> obtenerVenta(@PathVariable Integer id) {
        return ventaDAO.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /** Crea una venta y responde 201 con el registro creado. */
    @PostMapping
    public ResponseEntity<VentaVO> crearVenta(@RequestBody VentaVO venta) {
        VentaVO guardada = ventaDAO.save(venta);
        return ResponseEntity.status(HttpStatus.CREATED).body(guardada);
    }

    /** Actualiza la venta indicada por la ruta. */
    @PutMapping("/{id}")
    public ResponseEntity<VentaVO> actualizarVenta(@PathVariable Integer id, @RequestBody VentaVO venta) {
        venta.setIdVenta(id);
        VentaVO actualizada = ventaDAO.update(venta);
        return ResponseEntity.ok(actualizada);
    }

    /** Elimina la venta o responde 404 si no existía. */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarVenta(@PathVariable Integer id) {
        boolean eliminado = ventaDAO.deleteById(id);
        return eliminado ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}
