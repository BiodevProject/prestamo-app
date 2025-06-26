package com.biovizion.prestamo911.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import com.biovizion.prestamo911.entities.TrabajadorEntity;
import com.biovizion.prestamo911.service.TrabajadorService;

import java.util.List;

@Controller
@RequestMapping("/trabajador")
public class TrabajadorController {

    @Autowired
    private TrabajadorService trabajadorService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/create")
    public String createTrabajador(Model model) {
        model.addAttribute("trabajador", new TrabajadorEntity());
        return "trabajador/trabajadorForm";
    }

    @GetMapping("/dashboard")
    public String ShowListaTrabajadores(Model model) {
        List<TrabajadorEntity> trabajadores = trabajadorService.findAll();
        model.addAttribute("trabajadores", trabajadores);
        return "trabajador/trabajadorDashboard";
    }
    
    @GetMapping("/edit/{id}")
    public String editTrabajador(@PathVariable Long id, Model model) {
        TrabajadorEntity trabajador = trabajadorService.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        model.addAttribute("trabajador", trabajador);
        return "trabajador/trabajadorEdit";
    }

    @PostMapping("/save")
    public String saveTrabajador(@ModelAttribute TrabajadorEntity trabajador, Model model) {
        System.out.println("Guardando trabajador: " + trabajador.getEmail());
        if (trabajadorService.findByEmail(trabajador.getEmail()).isPresent()) {
            model.addAttribute("error", "El email ya está registrado");
            return "auth/registro";
        }

        trabajador.setPassword(passwordEncoder.encode(trabajador.getPassword()));

        trabajadorService.save(trabajador);
        return "redirect:/auth/login";
    }

    @PostMapping("/update")
    public String updateTrabajador(@ModelAttribute TrabajadorEntity trabajador) {

        // Obtener el trabajador existente y conservar la contraseña
        TrabajadorEntity trabajadorExistente = trabajadorService.findById(trabajador.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        trabajador.setPassword(trabajadorExistente.getPassword());

        trabajadorService.update(trabajador);
        return "redirect:/trabajador/dashboard";
    }

    @PostMapping("/delete/{id}")
    public String deleteTrabajador(@PathVariable Long id) {
        trabajadorService.delete(id);
        return "redirect:/trabajador/dashboard";
    }
} 