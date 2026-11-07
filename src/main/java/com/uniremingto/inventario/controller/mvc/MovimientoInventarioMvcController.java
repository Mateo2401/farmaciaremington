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

@Controller
@RequestMapping("/movimientos-inventario")
public class MovimientoInventarioMvcController {

    private final MovimientoInventarioDAO movimientoInventarioDAO;

    public MovimientoInventarioMvcController(MovimientoInventarioDAO movimientoInventarioDAO) {
        this.movimientoInventarioDAO = movimientoInventarioDAO;
    }

    @GetMapping
    public String listarMovimientos(Model model) {
        model.addAttribute("movimientosInventario", movimientoInventarioDAO.findAll());
        return "movimientos-inventario/lista";
    }

    @GetMapping("/nuevo")
    public String mostrarFormularioNuevo(Model model) {
        model.addAttribute("movimientoInventario", new MovimientoInventarioVO());
        return "movimientos-inventario/formulario";
    }

    @PostMapping
    public String guardarMovimiento(@ModelAttribute MovimientoInventarioVO movimientoInventario) {
        movimientoInventarioDAO.save(movimientoInventario);
        return "redirect:/movimientos-inventario";
    }

    @GetMapping("/{id}/editar")
    public String mostrarFormularioEdicion(@PathVariable Integer id, Model model) {
        movimientoInventarioDAO.findById(id).ifPresent(movimiento -> model.addAttribute("movimientoInventario", movimiento));
        return "movimientos-inventario/formulario";
    }

    @PostMapping("/{id}/eliminar")
    public String eliminarMovimiento(@PathVariable Integer id) {
        movimientoInventarioDAO.deleteById(id);
        return "redirect:/movimientos-inventario";
    }
}
