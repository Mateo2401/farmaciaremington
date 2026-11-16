package com.uniremingto.inventario.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/** Atiende las páginas de autenticación web. */
@Controller
public class AuthController {

    /** Muestra el formulario de inicio de sesión. */
    @GetMapping("/login")
    public String login() {
        return "login";
    }
}
