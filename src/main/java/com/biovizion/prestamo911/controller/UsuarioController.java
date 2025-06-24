package com.biovizion.prestamo911.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import com.biovizion.prestamo911.entities.UsuarioEntity;
import com.biovizion.prestamo911.service.UsuarioService;

import java.util.List;

@Controller
@RequestMapping("/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/create")
    public String createUser(Model model) {
        model.addAttribute("usuario", new UsuarioEntity());
        return "auth/register";
    }

    @GetMapping("/dashboard")
    public String ShowListaUsuarios(Model model) {
        List<UsuarioEntity> usuarios = usuarioService.findAll();
        model.addAttribute("usuarios", usuarios);
        return "usuario/userDashboard";
    }
    
    @GetMapping("/edit/{id}")
    public String editUsuario(@PathVariable Long id, Model model) {
        UsuarioEntity usuario = usuarioService.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        model.addAttribute("usuario", usuario);
        return "usuario/userEdit";
    }

    @PostMapping("/save")
    public String saveUsuario(@ModelAttribute UsuarioEntity usuario, Model model) {
        System.out.println("Guardando usuario: " + usuario.getEmail());
        if (usuarioService.findByEmail(usuario.getEmail()).isPresent()) {
            model.addAttribute("error", "El email ya está registrado");
            return "auth/registro";
        }

        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));

        // Asignar rol directamente
        usuario.setRol("USER");

        usuarioService.save(usuario);
        return "redirect:/auth/login";
    }

    @PostMapping("/update")
    public String updateUsuario(@ModelAttribute UsuarioEntity usuario) {

        // Obtener el usuario existente y conservar la contraseña
        UsuarioEntity usuarioExistente = usuarioService.findById(usuario.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        usuario.setPassword(usuarioExistente.getPassword());

        usuarioService.update(usuario);
        return "redirect:/usuarios/dashboard";
    }

    @PostMapping("/delete/{id}")
    public String deleteUsuario(@PathVariable Long id) {
        usuarioService.delete(id);
        return "redirect:/usuarios/dashboard";
    }
}
