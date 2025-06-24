package com.biovizion.prestamo911.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.biovizion.prestamo911.entities.UsuarioEntity;

import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequestMapping("/auth")
public class LoginController {

    @GetMapping("/login")
    public String vistalogin() {
        return "auth/login";
    }

    @GetMapping("/register")
    public String vistaregistro(Model model) {
        model.addAttribute("usuario", new UsuarioEntity());
        return "auth/register";
    }
}





