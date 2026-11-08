package com.uniremingto.inventario.controller.mvc;

import com.uniremingto.inventario.model.dao.VentaDAO;
import com.uniremingto.inventario.model.vo.VentaVO;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/** Atiende las vistas MVC de gestión de ventas. */
@Controller
@RequestMapping("/ventas")
public class VentaMvcController {

    private final VentaDAO ventaDAO;

    public VentaMvcController(VentaDAO ventaDAO) {
        this.ventaDAO = ventaDAO;
    }

    /** Carga las ventas para la vista de lista. */
    @GetMapping
    public String listarVentas(Model model) {
        model.addAttribute("ventas", ventaDAO.findAll());
        return "ventas/lista";
    }

    /** Prepara un objeto vacío para registrar una venta. */
    @GetMapping("/nuevo")
    public String mostrarFormularioNuevo(Model model) {
        model.addAttribute("venta", new VentaVO());
        return "ventas/formulario";
    }

    /** Envía la venta recibida al DAO y redirige al listado. */
    @PostMapping
    public String guardarVenta(@ModelAttribute VentaVO venta) {
        ventaDAO.save(venta);
        return "redirect:/ventas";
    }

    /** Carga una venta existente para el formulario de edición. */
    @GetMapping("/{id}/editar")
    public String mostrarFormularioEdicion(@PathVariable Integer id, Model model) {
        ventaDAO.findById(id).ifPresent(venta -> model.addAttribute("venta", venta));
        return "ventas/formulario";
    }

    /** Solicita al DAO la eliminación de una venta. */
    @PostMapping("/{id}/eliminar")
    public String eliminarVenta(@PathVariable Integer id) {
        ventaDAO.deleteById(id);
        return "redirect:/ventas";
    }
}
