package com.biovizion.prestamo911.controller;

import com.biovizion.prestamo911.entities.TrabajadorEntity;
import com.biovizion.prestamo911.entities.UsuarioEntity;
import com.biovizion.prestamo911.entities.CreditoEntity;
import com.biovizion.prestamo911.service.TrabajadorService;
import com.biovizion.prestamo911.service.UsuarioService;
import com.biovizion.prestamo911.service.CreditoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("")
public class HomeController {

    @Autowired
    private UsuarioService usuarioService;
    
    @Autowired
    private TrabajadorService trabajadorService;

    @Autowired
    private CreditoService creditoService;

    @GetMapping("/home")
    public String mostrarIndex() {
        return "/home/home";
    }
    @GetMapping("/select")
    public String SelectoOption() {
        return "/home/landingPage";
    }

    @GetMapping("/admin/dashboard")
    public String mostrarUsuarioDashboard(Model model, Principal principal) {
        // Get current user's name
        String currentUserName = getCurrentUserName(principal);
        model.addAttribute("currentUserName", currentUserName);
        
        List<UsuarioEntity> usuarios = usuarioService.findAll();
        List<TrabajadorEntity> trabajadores = trabajadorService.findAll();
        model.addAttribute("usuarios", usuarios);
        model.addAttribute("trabajadores", trabajadores);
        return "/appDashboard/admin/index";
    }
    
    @GetMapping("/admin/usuarios")
    public String mostrarUsuarios(Model model, Principal principal) {
        // Get current user's name
        String currentUserName = getCurrentUserName(principal);
        model.addAttribute("currentUserName", currentUserName);
        
        List<UsuarioEntity> usuarios = usuarioService.findAll();
        model.addAttribute("usuarios", usuarios);
        return "/appDashboard/admin/usuarios";
    }
    
    @GetMapping("/admin/trabajadores")
    public String mostrarTrabajadores(Model model, Principal principal) {
        // Get current user's name
        String currentUserName = getCurrentUserName(principal);
        model.addAttribute("currentUserName", currentUserName);
        
        List<TrabajadorEntity> trabajadores = trabajadorService.findAll();
        model.addAttribute("trabajadores", trabajadores);
        return "/appDashboard/admin/trabajadores";
    }

    // ADMIN: Creditos Pendientes
    @GetMapping("/admin/creditos/pendientes")
    public String adminCreditosPendientes(Model model, Principal principal) {
        // Get current user's name
        String currentUserName = getCurrentUserName(principal);
        model.addAttribute("currentUserName", currentUserName);
        
        List<CreditoEntity> creditos = creditoService.findPendientes();
        model.addAttribute("creditos", creditos);
        return "appDashboard/admin/creditosPendientes";
    }

    // ADMIN: Creditos Aceptados
    @GetMapping("/admin/creditos/aceptados")
    public String adminCreditosAceptados(Model model, Principal principal) {
        // Get current user's name
        String currentUserName = getCurrentUserName(principal);
        model.addAttribute("currentUserName", currentUserName);
        
        List<CreditoEntity> creditos = creditoService.findAceptadas();
        model.addAttribute("creditos", creditos);
        return "appDashboard/admin/creditosAceptados";
    }
    
    // ADMIN: Credito Detalle Modal Content
    @GetMapping("/admin/creditos/detalle/{id}/modal")
    public String adminCreditoDetalleModal(@PathVariable Long id, Model model) {
        CreditoEntity credito = creditoService.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        
        model.addAttribute("credito", credito);
        return "appDashboard/admin/creditoDetalle";
    }
    
    // Helper method to get current user's name
    private String getCurrentUserName(Principal principal) {
        if (principal == null) {
            return "Usuario";
        }
        
        try {
            String email = principal.getName();
            UsuarioEntity usuario = usuarioService.findByEmail(email)
                    .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
            return usuario.getNombre() != null ? usuario.getNombre() : email;
        } catch (Exception e) {
            return principal.getName();
        }
    }
}