package com.biovizion.prestamo911.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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



    @GetMapping("/register")
    public String registrarTrabajador(Model model) {

        return "auth/trabajadorRegister";
    }




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
    @ResponseBody
    public ResponseEntity<?> saveTrabajador(@RequestBody TrabajadorEntity trabajador) {
        if (trabajadorService.findByEmail(trabajador.getEmail()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("El email ya está registrado");
        }

        trabajador.setPassword(passwordEncoder.encode(trabajador.getPassword()));
        trabajadorService.save(trabajador);

        return ResponseEntity.ok().build(); // solo confirmación de éxito
    }


    @PostMapping("/update")
    public String updateTrabajador(@ModelAttribute TrabajadorEntity trabajadorForm) {

        // Obtener el trabajador existente
        TrabajadorEntity trabajadorExistente = trabajadorService.findById(trabajadorForm.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        // Copiar los datos actualizados
        trabajadorExistente.setNombre(trabajadorForm.getNombre());
        trabajadorExistente.setEmail(trabajadorForm.getEmail());

        // Solo actualizar la contraseña si se escribió una nueva
        if (trabajadorForm.getPassword() != null && !trabajadorForm.getPassword().trim().isEmpty()) {
            trabajadorExistente.setPassword(passwordEncoder.encode(trabajadorForm.getPassword()));
        }

        trabajadorService.update(trabajadorExistente);

        return "redirect:/trabajador/dashboard";
    }


    @PostMapping("/delete/{id}")
    public String deleteTrabajador(@PathVariable Long id) {
        trabajadorService.delete(id);
        return "redirect:/trabajador/dashboard";
    }
} 