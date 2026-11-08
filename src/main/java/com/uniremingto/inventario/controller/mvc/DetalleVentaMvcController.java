package com.uniremingto.inventario.controller.mvc;

import com.uniremingto.inventario.model.dao.DetalleVentaDAO;
import com.uniremingto.inventario.model.vo.DetalleVentaVO;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/** Atiende las vistas MVC para consultar y gestionar líneas de venta. */
@Controller
@RequestMapping("/detalle-ventas")
public class DetalleVentaMvcController {

    private final DetalleVentaDAO detalleVentaDAO;

    public DetalleVentaMvcController(DetalleVentaDAO detalleVentaDAO) {
        this.detalleVentaDAO = detalleVentaDAO;
    }

    /** Carga los detalles para la vista de lista. */
    @GetMapping
    public String listarDetalles(Model model) {
        model.addAttribute("detallesVenta", detalleVentaDAO.findAll());
        return "detalle-ventas/lista";
    }

    /** Prepara un objeto vacío para agregar un detalle. */
    @GetMapping("/nuevo")
    public String mostrarFormularioNuevo(Model model) {
        model.addAttribute("detalleVenta", new DetalleVentaVO());
        return "detalle-ventas/formulario";
    }

    /** Envía el detalle recibido al DAO y redirige al listado. */
    @PostMapping
    public String guardarDetalle(@ModelAttribute DetalleVentaVO detalleVenta) {
        detalleVentaDAO.save(detalleVenta);
        return "redirect:/detalle-ventas";
    }

    /** Carga un detalle existente para el formulario de edición. */
    @GetMapping("/{id}/editar")
    public String mostrarFormularioEdicion(@PathVariable Integer id, Model model) {
        detalleVentaDAO.findById(id).ifPresent(detalle -> model.addAttribute("detalleVenta", detalle));
        return "detalle-ventas/formulario";
    }

    /** Solicita al DAO la eliminación de un detalle. */
    @PostMapping("/{id}/eliminar")
    public String eliminarDetalle(@PathVariable Integer id) {
        detalleVentaDAO.deleteById(id);
        return "redirect:/detalle-ventas";
    }
}
