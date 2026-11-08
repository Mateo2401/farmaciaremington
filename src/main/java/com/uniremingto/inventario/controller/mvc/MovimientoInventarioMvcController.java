package com.uniremingto.inventario.controller.mvc;

import com.uniremingto.inventario.model.dao.MovimientoInventarioDAO;
import com.uniremingto.inventario.model.vo.MovimientoInventarioVO;
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
        MovimientoInventarioVO movimiento = new MovimientoInventarioVO();
        movimiento.setTipo("ENTRADA");
        model.addAttribute("movimientoInventario", movimiento);
        return "movimientos-inventario/formulario";
    }

    /** Valida y registra un movimiento nuevo. */
    @PostMapping
    public String guardarMovimiento(@ModelAttribute MovimientoInventarioVO movimientoInventario,
                                    BindingResult bindingResult, Model model,
                                    RedirectAttributes redirectAttributes) {
        return persistirMovimiento(movimientoInventario, bindingResult, model, redirectAttributes, false);
    }

    /** Actualiza el movimiento identificado en la ruta. */
    @PostMapping("/{id}")
    public String actualizarMovimiento(@PathVariable Integer id,
                                       @ModelAttribute MovimientoInventarioVO movimientoInventario,
                                       BindingResult bindingResult, Model model,
                                       RedirectAttributes redirectAttributes) {
        movimientoInventario.setIdMovimiento(id);
        return persistirMovimiento(movimientoInventario, bindingResult, model, redirectAttributes, true);
    }

    private String persistirMovimiento(MovimientoInventarioVO movimiento, BindingResult bindingResult,
                                       Model model, RedirectAttributes redirectAttributes, boolean edicion) {
        if (movimiento.getIdProducto() == null || movimiento.getIdProducto() <= 0) {
            bindingResult.rejectValue("idProducto", "required", "Ingresa un ID de producto válido.");
        }
        if (movimiento.getIdUsuario() == null || movimiento.getIdUsuario() <= 0) {
            bindingResult.rejectValue("idUsuario", "required", "Ingresa un ID de usuario válido.");
        }
        if (movimiento.getTipo() == null
                || !Set.of("ENTRADA", "SALIDA", "AJUSTE").contains(movimiento.getTipo())) {
            bindingResult.rejectValue("tipo", "invalid", "Selecciona un tipo de movimiento válido.");
        }
        if (movimiento.getCantidad() == null || movimiento.getCantidad() <= 0) {
            bindingResult.rejectValue("cantidad", "invalid", "La cantidad debe ser mayor que cero.");
        }
        if (movimiento.getDescripcion() != null && movimiento.getDescripcion().length() > 255) {
            bindingResult.rejectValue("descripcion", "length", "La descripción no puede superar 255 caracteres.");
        }
        if (bindingResult.hasErrors()) {
            return "movimientos-inventario/formulario";
        }

        try {
            if (edicion) {
                movimientoInventarioDAO.update(movimiento);
            } else {
                movimientoInventarioDAO.save(movimiento);
            }
        } catch (DataIntegrityViolationException exception) {
            model.addAttribute("formError",
                    "Verifica que el producto y el usuario existan y que los datos sean válidos.");
            return "movimientos-inventario/formulario";
        }

        redirectAttributes.addFlashAttribute("mensaje",
                edicion ? "El movimiento fue actualizado." : "El movimiento fue registrado.");
        return "redirect:/movimientos-inventario";
    }

    /** Carga un movimiento existente para el formulario de edición. */
    @GetMapping("/{id}/editar")
    public String mostrarFormularioEdicion(@PathVariable Integer id, Model model,
                                           RedirectAttributes redirectAttributes) {
        var movimiento = movimientoInventarioDAO.findById(id);
        if (movimiento.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "El movimiento solicitado no existe.");
            return "redirect:/movimientos-inventario";
        }
        model.addAttribute("movimientoInventario", movimiento.get());
        return "movimientos-inventario/formulario";
    }

    /** Solicita al DAO la eliminación de un movimiento. */
    @PostMapping("/{id}/eliminar")
    public String eliminarMovimiento(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        if (movimientoInventarioDAO.deleteById(id)) {
            redirectAttributes.addFlashAttribute("mensaje", "El movimiento fue eliminado.");
        } else {
            redirectAttributes.addFlashAttribute("error", "El movimiento ya no existe.");
        }
        return "redirect:/movimientos-inventario";
    }
}
