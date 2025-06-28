package com.biovizion.prestamo911.controller;

import java.util.List;

import com.biovizion.prestamo911.entities.UsuarioEntity;
import com.biovizion.prestamo911.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;

import com.biovizion.prestamo911.entities.CreditoEntity;
import com.biovizion.prestamo911.entities.UsuarioSolicitudEntity;
import com.biovizion.prestamo911.service.CreditoService;

@Controller
@RequestMapping("/credito")
public class CreditoController {
    @Autowired
    private CreditoService creditoService;

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping("/form")
    public String creditoForm(Model model) {
        CreditoEntity credito = new CreditoEntity();
        credito.setUsuarioSolicitud(new UsuarioSolicitudEntity()); // initialize nested object

        model.addAttribute("credito", credito);
        return "credito/creditoForm";
    }

    @PostMapping("/save")
    public String saveCredito(@ModelAttribute CreditoEntity credito) {

        // Obtener la sesión actual
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName(); // normalmente el email es el username

        // Buscar el usuario desde tu servicio por su email
        UsuarioEntity usuario = usuarioService.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con email: " + email));


        // Asociar el crédito al usuario autenticado
        credito.setUsuario(usuario);

        // Guardar el crédito
        creditoService.save(credito);

        return "redirect:/usuario/estadoDeCreditos";
    }


    @GetMapping("/dashboardPendientes")
    public String creditoDashboardPendientes(Model model) {
        List<CreditoEntity> creditos = creditoService.findPendientes();
        model.addAttribute("creditos", creditos);

        return "credito/creditoDashboardPendientes";
    }

    @GetMapping("/dashboardAceptadas")
    public String creditoDashboardAceptados(Model model) {
        List<CreditoEntity> creditos = creditoService.findAceptadas();
        model.addAttribute("creditos", creditos);

        return "credito/creditoDashboardAceptadas";
    }


    @GetMapping("/edit/{id}")
    public String creditoEdit(@PathVariable Long id, Model model){
        CreditoEntity credito = creditoService.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        model.addAttribute("credito", credito);
        return "credito/creditoEdit";
    }
    @PostMapping("/update")
    public String creditoUpdate(@ModelAttribute CreditoEntity credito) {
        creditoService.update(credito);
        return "redirect:/credito/edit/" + credito.getId();
    }

    @PostMapping("/delete/{id}")
    public String creditoDelete(@PathVariable Long id) {
        creditoService.delete(id);
        return "redirect:/credito/dashboard";
    }

    @PostMapping("/accept/{id}")
    public String acceptCredito(@PathVariable Long id) {
        CreditoEntity credito = creditoService.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        
        credito.setEstado("aceptado");
        creditoService.update(credito);
        
        return "redirect:/admin/creditos/pendientes";
    }
}