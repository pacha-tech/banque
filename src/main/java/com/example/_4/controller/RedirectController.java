package com.example._4.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class RedirectController {

    @GetMapping("/")
    public String redirectToSwagger() {
        // Redirige l'URL racine vers l'URL par défaut de Swagger UI
        return "redirect:/swagger-ui.html";
    }
}

