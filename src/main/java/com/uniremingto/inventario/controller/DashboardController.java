package com.uniremingto.inventario.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/** Prepara el modelo de la página principal para el usuario autenticado. */
@Controller
public class DashboardController {

    /** Obtiene el usuario y sus autoridades para mostrarlos en el dashboard. */
    @GetMapping({"/", "/dashboard"})
    public String dashboard(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        model.addAttribute("username", authentication.getName());
        model.addAttribute("roles", authentication.getAuthorities());
        return "dashboard";
    }
}
