package com.uniremingto.inventario.controller.mvc;

import com.uniremingto.inventario.model.dao.ProveedorDAO;
import com.uniremingto.inventario.model.vo.ProveedorVO;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/proveedores")
public class ProveedorMvcController {

    private final ProveedorDAO proveedorDAO;

    public ProveedorMvcController(ProveedorDAO proveedorDAO) {
        this.proveedorDAO = proveedorDAO;
    }

    @GetMapping
    public String listarProveedores(Model model) {
        model.addAttribute("proveedores", proveedorDAO.findAll());
        return "proveedores/lista";
    }

    @GetMapping("/nuevo")
    public String mostrarFormularioNuevo(Model model) {
        model.addAttribute("proveedor", new ProveedorVO());
        return "proveedores/formulario";
    }

    @PostMapping
    public String guardarProveedor(@ModelAttribute ProveedorVO proveedor) {
        proveedorDAO.save(proveedor);
        return "redirect:/proveedores";
    }

    @GetMapping("/{id}/editar")
    public String mostrarFormularioEdicion(@PathVariable Integer id, Model model) {
        proveedorDAO.findById(id).ifPresent(proveedor -> model.addAttribute("proveedor", proveedor));
        return "proveedores/formulario";
    }

    @PostMapping("/{id}/eliminar")
    public String eliminarProveedor(@PathVariable Integer id) {
        proveedorDAO.deleteById(id);
        return "redirect:/proveedores";
    }
}
