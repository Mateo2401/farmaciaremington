package com.uniremingto.inventario.controller.mvc;

import com.uniremingto.inventario.model.dao.VentaDAO;
import com.uniremingto.inventario.model.vo.VentaVO;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Set;

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
        VentaVO venta = new VentaVO();
        venta.setEstado("COMPLETADA");
        model.addAttribute("venta", venta);
        return "ventas/formulario";
    }

    /** Valida y guarda una venta nueva. */
    @PostMapping
    public String guardarVenta(@ModelAttribute VentaVO venta, BindingResult bindingResult,
                               Model model, RedirectAttributes redirectAttributes) {
        return persistirVenta(venta, bindingResult, model, redirectAttributes, false);
    }

    /** Actualiza la venta identificada por la ruta. */
    @PostMapping("/{id}")
    public String actualizarVenta(@PathVariable Integer id, @ModelAttribute VentaVO venta,
                                  BindingResult bindingResult, Model model,
                                  RedirectAttributes redirectAttributes) {
        venta.setIdVenta(id);
        return persistirVenta(venta, bindingResult, model, redirectAttributes, true);
    }

    private String persistirVenta(VentaVO venta, BindingResult bindingResult, Model model,
                                  RedirectAttributes redirectAttributes, boolean edicion) {
        if (venta.getIdUsuario() == null || venta.getIdUsuario() <= 0) {
            bindingResult.rejectValue("idUsuario", "required", "Ingresa un ID de usuario válido.");
        }
        if (venta.getTotal() == null || venta.getTotal().signum() < 0) {
            bindingResult.rejectValue("total", "invalid", "El total debe ser igual o mayor que cero.");
        }
        if (venta.getMetodoPago() == null
            || !Set.of("EFECTIVO", "TARJETA", "TRANSFERENCIA").contains(venta.getMetodoPago())) {
            bindingResult.rejectValue("metodoPago", "invalid", "Selecciona un método de pago válido.");
        }
        if (bindingResult.hasErrors()) {
            return "ventas/formulario";
        }
        if (venta.getEstado() == null || venta.getEstado().isBlank()) {
            venta.setEstado("COMPLETADA");
        }
        if (!Set.of("COMPLETADA", "ANULADA").contains(venta.getEstado())) {
            bindingResult.rejectValue("estado", "invalid", "Selecciona un estado válido.");
            return "ventas/formulario";
        }

        try {
            if (edicion) {
                ventaDAO.update(venta);
            } else {
                ventaDAO.save(venta);
            }
        } catch (DataIntegrityViolationException exception) {
            model.addAttribute("formError",
                    "Verifica que el usuario exista y que los datos cumplan las reglas de la base de datos.");
            return "ventas/formulario";
        }

        redirectAttributes.addFlashAttribute("mensaje",
                edicion ? "La venta fue actualizada." : "La venta fue registrada.");
        return "redirect:/ventas";
    }

    /** Carga una venta existente para el formulario de edición. */
    @GetMapping("/{id}/editar")
    public String mostrarFormularioEdicion(@PathVariable Integer id, Model model,
                                           RedirectAttributes redirectAttributes) {
        var venta = ventaDAO.findById(id);
        if (venta.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "La venta solicitada no existe.");
            return "redirect:/ventas";
        }
        model.addAttribute("venta", venta.get());
        return "ventas/formulario";
    }

    /** Solicita al DAO la eliminación de una venta. */
    @PostMapping("/{id}/eliminar")
    public String eliminarVenta(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        try {
            if (ventaDAO.deleteById(id)) {
                redirectAttributes.addFlashAttribute("mensaje", "La venta fue eliminada.");
            } else {
                redirectAttributes.addFlashAttribute("error", "La venta ya no existe.");
            }
        } catch (DataIntegrityViolationException exception) {
            redirectAttributes.addFlashAttribute("error",
                    "No se puede eliminar la venta porque tiene detalles asociados.");
        }
        return "redirect:/ventas";
    }
}
