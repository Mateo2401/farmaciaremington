package com.uniremingto.inventario.controller.res;

import com.uniremingto.inventario.model.dao.DetalleVentaDAO;
import com.uniremingto.inventario.model.vo.DetalleVentaVO;
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
@RequestMapping("/api/detalle-ventas")
public class DetalleVentaRestController {

    private final DetalleVentaDAO detalleVentaDAO;

    public DetalleVentaRestController(DetalleVentaDAO detalleVentaDAO) {
        this.detalleVentaDAO = detalleVentaDAO;
    }

    @GetMapping
    public List<DetalleVentaVO> listarDetalles() {
        return detalleVentaDAO.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<DetalleVentaVO> obtenerDetalle(@PathVariable Integer id) {
        return detalleVentaDAO.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<DetalleVentaVO> crearDetalle(@RequestBody DetalleVentaVO detalleVenta) {
        DetalleVentaVO guardado = detalleVentaDAO.save(detalleVenta);
        return ResponseEntity.status(HttpStatus.CREATED).body(guardado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DetalleVentaVO> actualizarDetalle(@PathVariable Integer id, @RequestBody DetalleVentaVO detalleVenta) {
        detalleVenta.setIdDetalle(id);
        DetalleVentaVO actualizado = detalleVentaDAO.update(detalleVenta);
        return ResponseEntity.ok(actualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarDetalle(@PathVariable Integer id) {
        boolean eliminado = detalleVentaDAO.deleteById(id);
        return eliminado ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}
