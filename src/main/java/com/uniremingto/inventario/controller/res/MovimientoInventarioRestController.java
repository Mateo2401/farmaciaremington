package com.uniremingto.inventario.controller.res;

import com.uniremingto.inventario.model.dao.MovimientoInventarioDAO;
import com.uniremingto.inventario.model.vo.MovimientoInventarioVO;
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
@RequestMapping("/api/movimientos-inventario")
public class MovimientoInventarioRestController {

    private final MovimientoInventarioDAO movimientoInventarioDAO;

    public MovimientoInventarioRestController(MovimientoInventarioDAO movimientoInventarioDAO) {
        this.movimientoInventarioDAO = movimientoInventarioDAO;
    }

    @GetMapping
    public List<MovimientoInventarioVO> listarMovimientos() {
        return movimientoInventarioDAO.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<MovimientoInventarioVO> obtenerMovimiento(@PathVariable Integer id) {
        return movimientoInventarioDAO.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<MovimientoInventarioVO> crearMovimiento(@RequestBody MovimientoInventarioVO movimientoInventario) {
        MovimientoInventarioVO guardado = movimientoInventarioDAO.save(movimientoInventario);
        return ResponseEntity.status(HttpStatus.CREATED).body(guardado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MovimientoInventarioVO> actualizarMovimiento(@PathVariable Integer id, @RequestBody MovimientoInventarioVO movimientoInventario) {
        movimientoInventario.setIdMovimiento(id);
        MovimientoInventarioVO actualizado = movimientoInventarioDAO.update(movimientoInventario);
        return ResponseEntity.ok(actualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarMovimiento(@PathVariable Integer id) {
        boolean eliminado = movimientoInventarioDAO.deleteById(id);
        return eliminado ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}
