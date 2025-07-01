package com.biovizion.prestamo911.controller;


import com.biovizion.prestamo911.entities.UsuarioEntity;
import com.biovizion.prestamo911.service.EmailService;
import com.biovizion.prestamo911.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Random;

@Controller
@RequestMapping("/verificacion")
public class VerificacionController {


    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private EmailService emailService;


    @GetMapping("/verificar")
    public String mostrarFormularioVerificacion(@RequestParam String email, Model model) {
        model.addAttribute("email", email);
        return "auth/verificar"; // La vista Thymeleaf que vamos a crear
    }

    @PostMapping("/verificar")
    public String verificarCodigo(@RequestParam String email,
                                  @RequestParam String codigo,
                                  Model model) {
        UsuarioEntity usuario = usuarioService.findByEmail(email)
                .orElse(null);
        if (usuario == null) {
            model.addAttribute("error", "Usuario no encontrado");
            return "auth/verificar";
        }
        if (!usuario.getCodigo().equals(codigo)) {
            model.addAttribute("error", "Código incorrecto");
            model.addAttribute("email", email);
            return "auth/verificar";
        }
        usuario.setActivo(true);
        usuario.setCodigo(null);
        usuarioService.save(usuario);

        return "redirect:/auth/login?verificado=true";
    }

    @PostMapping("/reenviar-codigo")
    public String reenviarCodigo(@RequestParam String email, Model model) {
        UsuarioEntity usuario = usuarioService.findByEmail(email)
                .orElse(null);
        if (usuario == null) {
            model.addAttribute("error", "Usuario no encontrado");
            return "auth/verificar";
        }
        String codigo = String.format("%06d", new Random().nextInt(1000000));
        usuario.setCodigo(codigo);
        usuarioService.save(usuario);
        emailService.enviarCodigoVerificacion(email, codigo);

        model.addAttribute("mensaje", "Código reenviado correctamente");
        model.addAttribute("email", email);
        return "auth/verificar";
    }


}
