package com.uniremingto.inventario.controller.mvc;

import com.uniremingto.inventario.model.dao.UsuarioDAO;
import com.uniremingto.inventario.model.vo.UsuarioVO;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/** Atiende las vistas MVC de gestión de usuarios. */
@Controller
@RequestMapping("/usuarios")
public class UsuarioMvcController {

    private final UsuarioDAO usuarioDAO;

    public UsuarioMvcController(UsuarioDAO usuarioDAO) {
        this.usuarioDAO = usuarioDAO;
    }

    /** Carga los usuarios para la vista de lista. */
    @GetMapping
    public String listarUsuarios(Model model) {
        model.addAttribute("usuarios", usuarioDAO.findAll());
        return "usuarios/lista";
    }

    /** Prepara un objeto vacío para crear un usuario. */
    @GetMapping("/nuevo")
    public String mostrarFormularioNuevo(Model model) {
        model.addAttribute("usuario", new UsuarioVO());
        return "usuarios/formulario";
    }

    /** Envía el usuario recibido al DAO y redirige al listado. */
    @PostMapping
    public String guardarUsuario(@ModelAttribute UsuarioVO usuario) {
        usuarioDAO.save(usuario);
        return "redirect:/usuarios";
    }

    /** Carga un usuario existente para el formulario de edición. */
    @GetMapping("/{id}/editar")
    public String mostrarFormularioEdicion(@PathVariable Integer id, Model model) {
        usuarioDAO.findById(id).ifPresent(usuario -> model.addAttribute("usuario", usuario));
        return "usuarios/formulario";
    }

    /** Solicita al DAO la eliminación de un usuario. */
    @PostMapping("/{id}/eliminar")
    public String eliminarUsuario(@PathVariable Integer id) {
        usuarioDAO.deleteById(id);
        return "redirect:/usuarios";
    }
}
