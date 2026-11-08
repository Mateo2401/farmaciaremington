package com.uniremingto.inventario.controller.mvc;

import com.uniremingto.inventario.model.dao.CategoriaDAO;
import com.uniremingto.inventario.model.vo.CategoriaVO;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

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
    public String guardarCategoria(@ModelAttribute CategoriaVO categoria) {
        categoriaDAO.save(categoria);
        return "redirect:/categorias";
    }

    /** Carga una categoría existente para el formulario de edición. */
    @GetMapping("/{id}/editar")
    public String mostrarFormularioEdicion(@PathVariable Integer id, Model model) {
        categoriaDAO.findById(id).ifPresent(categoria -> model.addAttribute("categoria", categoria));
        return "categorias/formulario";
    }

    /** Solicita al DAO la eliminación de una categoría. */
    @PostMapping("/{id}/eliminar")
    public String eliminarCategoria(@PathVariable Integer id) {
        categoriaDAO.deleteById(id);
        return "redirect:/categorias";
    }
}
