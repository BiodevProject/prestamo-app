package com.biovizion.prestamo911.controller;

import com.biovizion.prestamo911.entities.TrabajadorEntity;
import com.biovizion.prestamo911.entities.UsuarioEntity;
import com.biovizion.prestamo911.service.TrabajadorService;
import com.biovizion.prestamo911.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("")
public class HomeController {

    @Autowired
    private UsuarioService usuarioService;
    
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
    
    @GetMapping("/alex/dashboard")
    public String mostrarAdminDashboard() {
        return "/appDashboard/admin/index";
    }
    @GetMapping("/usuarioTemp/dashboard")
    public String mostrarUsuarioDashboard(Model model) {
        List<UsuarioEntity> usuarios = usuarioService.findAll();
        List<TrabajadorEntity> trabajadores = trabajadorService.findAll();
        model.addAttribute("usuarios", usuarios);
        model.addAttribute("trabajadores", trabajadores);
        return "/appDashboard/admin/index";
    }
    
    @GetMapping("/usuarioTemp/usuarios")
    public String mostrarUsuarios(Model model) {
        List<UsuarioEntity> usuarios = usuarioService.findAll();
        model.addAttribute("usuarios", usuarios);
        return "/appDashboard/admin/usuarios";
    }
    
    @GetMapping("/usuarioTemp/trabajadores")
    public String mostrarTrabajadores(Model model) {
        List<TrabajadorEntity> trabajadores = trabajadorService.findAll();
        model.addAttribute("trabajadores", trabajadores);
        return "/appDashboard/admin/trabajadores";
    }
}