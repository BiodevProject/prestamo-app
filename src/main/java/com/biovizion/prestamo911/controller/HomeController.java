package com.biovizion.prestamo911.controller;

import com.biovizion.prestamo911.entities.TrabajadorEntity;
import com.biovizion.prestamo911.service.TrabajadorService;
import com.biovizion.prestamo911.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("")
public class HomeController {

    @Autowired
    private TrabajadorService trabajadorService;

    @GetMapping("/home")
    public String mostrarIndex() {
        return "/home/home";
    }
    @GetMapping("/select")
    public String SelectoOption() {
        return "/home/landingPage";
    }
    
    @GetMapping("/admin/trabajadores")
    public String mostrarTrabajadores(Model model, Principal principal) {
        List<TrabajadorEntity> trabajadores = trabajadorService.findAll();
        model.addAttribute("trabajadores", trabajadores);
        return "/appDashboard/admin/trabajadores";
    }
}