package com.uniremingto.inventario.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    @GetMapping({"/", "/dashboard"})
    public String dashboard(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        model.addAttribute("username", authentication.getName());
        model.addAttribute("roles", authentication.getAuthorities());
        return "dashboard";
    }
}
