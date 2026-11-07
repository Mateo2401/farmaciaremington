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

@Controller
@RequestMapping("/usuarios")
public class UsuarioMvcController {

    private final UsuarioDAO usuarioDAO;

    public UsuarioMvcController(UsuarioDAO usuarioDAO) {
        this.usuarioDAO = usuarioDAO;
    }

    @GetMapping
    public String listarUsuarios(Model model) {
        model.addAttribute("usuarios", usuarioDAO.findAll());
        return "usuarios/lista";
    }

    @GetMapping("/nuevo")
    public String mostrarFormularioNuevo(Model model) {
        model.addAttribute("usuario", new UsuarioVO());
        return "usuarios/formulario";
    }

    @PostMapping
    public String guardarUsuario(@ModelAttribute UsuarioVO usuario) {
        usuarioDAO.save(usuario);
        return "redirect:/usuarios";
    }

    @GetMapping("/{id}/editar")
    public String mostrarFormularioEdicion(@PathVariable Integer id, Model model) {
        usuarioDAO.findById(id).ifPresent(usuario -> model.addAttribute("usuario", usuario));
        return "usuarios/formulario";
    }

    @PostMapping("/{id}/eliminar")
    public String eliminarUsuario(@PathVariable Integer id) {
        usuarioDAO.deleteById(id);
        return "redirect:/usuarios";
    }
}
