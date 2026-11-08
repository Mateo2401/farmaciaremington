package com.uniremingto.inventario.controller.mvc;

import com.uniremingto.inventario.model.dao.MovimientoInventarioDAO;
import com.uniremingto.inventario.model.vo.MovimientoInventarioVO;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/** Atiende las vistas MVC de consulta y registro de movimientos de inventario. */
@Controller
@RequestMapping("/movimientos-inventario")
public class MovimientoInventarioMvcController {

    private final MovimientoInventarioDAO movimientoInventarioDAO;

    public MovimientoInventarioMvcController(MovimientoInventarioDAO movimientoInventarioDAO) {
        this.movimientoInventarioDAO = movimientoInventarioDAO;
    }

    /** Carga los movimientos para la vista de historial. */
    @GetMapping
    public String listarMovimientos(Model model) {
        model.addAttribute("movimientosInventario", movimientoInventarioDAO.findAll());
        return "movimientos-inventario/lista";
    }

    /** Prepara un objeto vacío para registrar un movimiento. */
    @GetMapping("/nuevo")
    public String mostrarFormularioNuevo(Model model) {
        model.addAttribute("movimientoInventario", new MovimientoInventarioVO());
        return "movimientos-inventario/formulario";
    }

    /** Envía el movimiento recibido al DAO y redirige al historial. */
    @PostMapping
    public String guardarMovimiento(@ModelAttribute MovimientoInventarioVO movimientoInventario) {
        movimientoInventarioDAO.save(movimientoInventario);
        return "redirect:/movimientos-inventario";
    }

    /** Carga un movimiento existente para el formulario de edición. */
    @GetMapping("/{id}/editar")
    public String mostrarFormularioEdicion(@PathVariable Integer id, Model model) {
        movimientoInventarioDAO.findById(id).ifPresent(movimiento -> model.addAttribute("movimientoInventario", movimiento));
        return "movimientos-inventario/formulario";
    }

    /** Solicita al DAO la eliminación de un movimiento. */
    @PostMapping("/{id}/eliminar")
    public String eliminarMovimiento(@PathVariable Integer id) {
        movimientoInventarioDAO.deleteById(id);
        return "redirect:/movimientos-inventario";
    }
}
