package com.uniremingto.inventario.controller.mvc;

import com.uniremingto.inventario.model.dao.ProveedorDAO;
import com.uniremingto.inventario.model.vo.ProveedorVO;
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

/** Atiende las vistas MVC de gestión de proveedores. */
@Controller
@RequestMapping("/proveedores")
public class ProveedorMvcController {

    private final ProveedorDAO proveedorDAO;

    public ProveedorMvcController(ProveedorDAO proveedorDAO) {
        this.proveedorDAO = proveedorDAO;
    }

    /** Carga los proveedores para la vista de lista. */
    @GetMapping
    public String listarProveedores(Model model) {
        model.addAttribute("proveedores", proveedorDAO.findAll());
        return "proveedores/lista";
    }

    /** Prepara un objeto vacío para crear un proveedor. */
    @GetMapping("/nuevo")
    public String mostrarFormularioNuevo(Model model) {
        ProveedorVO proveedor = new ProveedorVO();
        proveedor.setEstado(true);
        model.addAttribute("proveedor", proveedor);
        return "proveedores/formulario";
    }

    /** Valida y persiste un proveedor nuevo. */
    @PostMapping
    public String guardarProveedor(@ModelAttribute ProveedorVO proveedor, BindingResult bindingResult,
                                   Model model, RedirectAttributes redirectAttributes) {
        return persistirProveedor(proveedor, bindingResult, model, redirectAttributes, false);
    }

    /** Actualiza el proveedor identificado en la ruta. */
    @PostMapping("/{id}")
    public String actualizarProveedor(@PathVariable Integer id, @ModelAttribute ProveedorVO proveedor,
                                      BindingResult bindingResult, Model model,
                                      RedirectAttributes redirectAttributes) {
        proveedor.setIdProveedor(id);
        return persistirProveedor(proveedor, bindingResult, model, redirectAttributes, true);
    }

    private String persistirProveedor(ProveedorVO proveedor, BindingResult bindingResult, Model model,
                                      RedirectAttributes redirectAttributes, boolean edicion) {
        if (proveedor.getNombre() == null || proveedor.getNombre().isBlank()) {
            bindingResult.rejectValue("nombre", "required", "El nombre del proveedor es obligatorio.");
        } else {
            proveedor.setNombre(proveedor.getNombre().trim());
            if (proveedor.getNombre().length() > 120) {
                bindingResult.rejectValue("nombre", "length", "El nombre no puede superar 120 caracteres.");
            }
        }
        if (bindingResult.hasErrors()) {
            return "proveedores/formulario";
        }
        if (proveedor.getEstado() == null) {
            proveedor.setEstado(true);
        }

        try {
            if (edicion) {
                proveedorDAO.update(proveedor);
            } else {
                proveedorDAO.save(proveedor);
            }
        } catch (DataIntegrityViolationException exception) {
            model.addAttribute("formError", "Verifica los datos del proveedor e intenta nuevamente.");
            return "proveedores/formulario";
        }

        redirectAttributes.addFlashAttribute("mensaje",
                edicion ? "El proveedor fue actualizado." : "El proveedor fue guardado.");
        return "redirect:/proveedores";
    }

    /** Carga un proveedor existente para el formulario de edición. */
    @GetMapping("/{id}/editar")
    public String mostrarFormularioEdicion(@PathVariable Integer id, Model model,
                                           RedirectAttributes redirectAttributes) {
        var proveedor = proveedorDAO.findById(id);
        if (proveedor.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "El proveedor solicitado no existe.");
            return "redirect:/proveedores";
        }
        model.addAttribute("proveedor", proveedor.get());
        return "proveedores/formulario";
    }

    /** Solicita al DAO la eliminación de un proveedor. */
    @PostMapping("/{id}/eliminar")
    public String eliminarProveedor(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        try {
            if (proveedorDAO.deleteById(id)) {
                redirectAttributes.addFlashAttribute("mensaje", "El proveedor fue eliminado.");
            } else {
                redirectAttributes.addFlashAttribute("error", "El proveedor ya no existe.");
            }
        } catch (DataIntegrityViolationException exception) {
            redirectAttributes.addFlashAttribute("error",
                    "No se puede eliminar el proveedor porque está asociado a productos.");
        }
        return "redirect:/proveedores";
    }
}
