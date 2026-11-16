package com.uniremingto.inventario.controller.mvc;

import com.uniremingto.inventario.model.dao.CategoriaDAO;
import com.uniremingto.inventario.model.vo.CategoriaVO;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/** Atiende las vistas MVC de gestión de categorías. */
@Controller
@RequestMapping("/categorias")
public class CategoriaMvcController {

    private final CategoriaDAO categoriaDAO;

    public CategoriaMvcController(CategoriaDAO categoriaDAO) {
        this.categoriaDAO = categoriaDAO;
    }

    /** Carga las categorías para la vista de lista. */
    @GetMapping
    public String listarCategorias(Model model) {
        model.addAttribute("categorias", categoriaDAO.findAll());
        return "categorias/lista";
    }

    /** Prepara un objeto vacío para crear una categoría. */
    @GetMapping("/nuevo")
    public String mostrarFormularioNuevo(Model model) {
        model.addAttribute("categoria", new CategoriaVO());
        return "categorias/formulario";
    }

    /** Envía la categoría recibida al DAO y redirige al listado. */
    @PostMapping
    public String guardarCategoria(@ModelAttribute CategoriaVO categoria, BindingResult bindingResult,
                                   Model model, RedirectAttributes redirectAttributes) {
        return persistirCategoria(categoria, bindingResult, model, redirectAttributes, false);
    }

    /** Actualiza la categoría indicada por la ruta. */
    @PostMapping("/{id}")
    public String actualizarCategoria(@PathVariable Integer id, @ModelAttribute CategoriaVO categoria,
                                      BindingResult bindingResult, Model model,
                                      RedirectAttributes redirectAttributes) {
        categoria.setIdCategoria(id);
        return persistirCategoria(categoria, bindingResult, model, redirectAttributes, true);
    }

    private String persistirCategoria(CategoriaVO categoria, BindingResult bindingResult, Model model,
                                      RedirectAttributes redirectAttributes, boolean edicion) {
        if (categoria.getNombre() == null || categoria.getNombre().isBlank()) {
            bindingResult.rejectValue("nombre", "required", "El nombre de la categoría es obligatorio.");
        } else {
            categoria.setNombre(categoria.getNombre().trim());
            if (categoria.getNombre().length() > 100) {
                bindingResult.rejectValue("nombre", "length", "El nombre no puede superar 100 caracteres.");
            }
        }
        if (categoria.getDescripcion() != null && categoria.getDescripcion().length() > 255) {
            bindingResult.rejectValue("descripcion", "length", "La descripción no puede superar 255 caracteres.");
        }
        if (bindingResult.hasErrors()) {
            return "categorias/formulario";
        }

        try {
            if (edicion) {
                categoriaDAO.update(categoria);
            } else {
                categoriaDAO.save(categoria);
            }
        } catch (DuplicateKeyException exception) {
            model.addAttribute("formError", "Ya existe una categoría con ese nombre.");
            return "categorias/formulario";
        } catch (DataIntegrityViolationException exception) {
            model.addAttribute("formError", "Verifica los datos de la categoría e intenta nuevamente.");
            return "categorias/formulario";
        }

        redirectAttributes.addFlashAttribute("mensaje",
                edicion ? "La categoría fue actualizada." : "La categoría fue guardada.");
        return "redirect:/categorias";
    }

    /** Carga una categoría existente para el formulario de edición. */
    @GetMapping("/{id}/editar")
    public String mostrarFormularioEdicion(@PathVariable Integer id, Model model,
                                           RedirectAttributes redirectAttributes) {
        var categoria = categoriaDAO.findById(id);
        if (categoria.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "La categoría solicitada no existe.");
            return "redirect:/categorias";
        }
        model.addAttribute("categoria", categoria.get());
        return "categorias/formulario";
    }

    /** Solicita al DAO la eliminación de una categoría. */
    @PostMapping("/{id}/eliminar")
    public String eliminarCategoria(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        try {
            if (categoriaDAO.deleteById(id)) {
                redirectAttributes.addFlashAttribute("mensaje", "La categoría fue eliminada.");
            } else {
                redirectAttributes.addFlashAttribute("error", "La categoría ya no existe.");
            }
        } catch (DataIntegrityViolationException exception) {
            redirectAttributes.addFlashAttribute("error",
                    "No se puede eliminar la categoría porque está asociada a productos.");
        }
        return "redirect:/categorias";
    }
}
