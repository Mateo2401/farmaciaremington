package com.uniremingto.inventario.controller.mvc;

import com.uniremingto.inventario.service.UsuarioService;
import com.uniremingto.inventario.model.vo.UsuarioVO;
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

import java.nio.charset.StandardCharsets;
import java.util.Set;

/** Atiende las vistas MVC de gestión de usuarios. */
@Controller
@RequestMapping("/usuarios")
public class UsuarioMvcController {

    private final UsuarioService usuarioService;

    public UsuarioMvcController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    /** Carga los usuarios para la vista de lista. */
    @GetMapping
    public String listarUsuarios(Model model) {
        model.addAttribute("usuarios", usuarioService.listar());
        return "usuarios/lista";
    }

    /** Prepara un objeto vacío para crear un usuario. */
    @GetMapping("/nuevo")
    public String mostrarFormularioNuevo(Model model) {
        UsuarioVO usuario = new UsuarioVO();
        usuario.setRol("VENDEDOR");
        usuario.setEstado(true);
        model.addAttribute("usuario", usuario);
        return "usuarios/formulario";
    }

    /** Valida, codifica la contraseña y persiste un usuario nuevo. */
    @PostMapping
    public String guardarUsuario(@ModelAttribute UsuarioVO usuario, BindingResult bindingResult,
                                 Model model, RedirectAttributes redirectAttributes) {
        return persistirUsuario(usuario, bindingResult, model, redirectAttributes, false);
    }

    /** Actualiza los datos del usuario indicado por la ruta. */
    @PostMapping("/{id}")
    public String actualizarUsuario(@PathVariable Integer id, @ModelAttribute UsuarioVO usuario,
                                    BindingResult bindingResult, Model model,
                                    RedirectAttributes redirectAttributes) {
        usuario.setIdUsuario(id);
        return persistirUsuario(usuario, bindingResult, model, redirectAttributes, true);
    }

    private String persistirUsuario(UsuarioVO usuario, BindingResult bindingResult, Model model,
                                    RedirectAttributes redirectAttributes, boolean edicion) {
        if (usuario.getNombre() == null || usuario.getNombre().isBlank()) {
            bindingResult.rejectValue("nombre", "required", "El nombre es obligatorio.");
        } else {
            usuario.setNombre(usuario.getNombre().trim());
            if (usuario.getNombre().length() > 100) {
                bindingResult.rejectValue("nombre", "length", "El nombre no puede superar 100 caracteres.");
            }
        }
        if (usuario.getCorreo() == null || usuario.getCorreo().isBlank()
                || !usuario.getCorreo().contains("@")) {
            bindingResult.rejectValue("correo", "invalid", "Ingresa un correo válido.");
        } else {
            usuario.setCorreo(usuario.getCorreo().trim());
            if (usuario.getCorreo().length() > 100) {
                bindingResult.rejectValue("correo", "length", "El correo no puede superar 100 caracteres.");
            }
        }
        if ((!edicion || usuario.getContrasena() != null && !usuario.getContrasena().isBlank())
                && (usuario.getContrasena() == null
                || usuario.getContrasena().getBytes(StandardCharsets.UTF_8).length < 8
                || usuario.getContrasena().getBytes(StandardCharsets.UTF_8).length > 72)) {
            bindingResult.rejectValue("contrasena", "invalid", "La contraseña debe tener entre 8 y 72 bytes.");
        }
        if (usuario.getRol() == null || !Set.of("ADMINISTRADOR", "VENDEDOR").contains(usuario.getRol())) {
            bindingResult.rejectValue("rol", "invalid", "Selecciona un rol válido.");
        }
        if (bindingResult.hasErrors()) {
            usuario.setContrasena(null);
            return "usuarios/formulario";
        }
        if (usuario.getEstado() == null) {
            usuario.setEstado(true);
        }

        try {
            if (edicion) {
                usuarioService.actualizar(usuario.getIdUsuario(), usuario);
            } else {
                usuarioService.crear(usuario);
            }
        } catch (DuplicateKeyException exception) {
            usuario.setContrasena(null);
            model.addAttribute("formError", "Ya existe un usuario registrado con ese correo.");
            return "usuarios/formulario";
        } catch (DataIntegrityViolationException exception) {
            usuario.setContrasena(null);
            model.addAttribute("formError", "Verifica los datos del usuario e intenta nuevamente.");
            return "usuarios/formulario";
        }

        redirectAttributes.addFlashAttribute("mensaje",
                edicion ? "El usuario fue actualizado." : "El usuario fue registrado.");
        return "redirect:/usuarios";
    }

    /** Carga un usuario existente para el formulario de edición. */
    @GetMapping("/{id}/editar")
    public String mostrarFormularioEdicion(@PathVariable Integer id, Model model,
                                           RedirectAttributes redirectAttributes) {
        var usuario = usuarioService.buscarPorId(id);
        if (usuario.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "El usuario solicitado no existe.");
            return "redirect:/usuarios";
        }
        usuario.get().setContrasena(null);
        model.addAttribute("usuario", usuario.get());
        return "usuarios/formulario";
    }

    /** Solicita al DAO la eliminación de un usuario. */
    @PostMapping("/{id}/eliminar")
    public String eliminarUsuario(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        try {
            if (usuarioService.eliminar(id)) {
                redirectAttributes.addFlashAttribute("mensaje", "El usuario fue eliminado.");
            } else {
                redirectAttributes.addFlashAttribute("error", "El usuario ya no existe.");
            }
        } catch (DataIntegrityViolationException exception) {
            redirectAttributes.addFlashAttribute("error",
                    "No se puede eliminar el usuario porque tiene ventas o movimientos asociados.");
        }
        return "redirect:/usuarios";
    }
}
