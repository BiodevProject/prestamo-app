package com.biovizion.prestamo911.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;

import com.biovizion.prestamo911.entities.UsuarioSolicitudEntity;
import com.biovizion.prestamo911.service.UsuarioSolicitudService;

@Controller
@RequestMapping("/usuarioSolicitud")
public class UsuarioSolicitudController {
    @Autowired
    private UsuarioSolicitudService usuarioSolicitudService;

    @PostMapping("/save")
    public String usuarioSolicitudSave(@ModelAttribute UsuarioSolicitudEntity usuarioSolicitud, Model model) {
        usuarioSolicitudService.save(usuarioSolicitud);
        return "redirect:/usuario/estadoDeCreditos";
    }

    @GetMapping("/dashboard")
    public String usuarioSolicitudDashboard(Model model) {
        List<UsuarioSolicitudEntity> usuarioSolicitudes = usuarioSolicitudService.findAll();
        model.addAttribute("usuarioSolicitudes", usuarioSolicitudes);

        return "usuarioSolicitud/usuarioSolicitudDashboard";
    }
    @GetMapping("/edit/{id}")
    public String usuarioSolicitudEdit(@PathVariable Long id, Model model){
        UsuarioSolicitudEntity usuarioSolicitud = usuarioSolicitudService.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        model.addAttribute("usuarioSolicitud", usuarioSolicitud);
        return "usuarioSolicitud/usuarioSolicitudEdit";
    }
    @PostMapping("/update")
    public String usuarioUpdate(@ModelAttribute UsuarioSolicitudEntity usuarioSolicitud) {
        usuarioSolicitudService.update(usuarioSolicitud);
        return "redirect:/usuarioSolicitud/edit/" + usuarioSolicitud.getId();
    }


    @PostMapping("/delete/{id}")
    public String usuarioSolicitudDelete(@PathVariable Long id) {
        usuarioSolicitudService.delete(id);
        return "redirect:/usuarioSolicitud/dashboard";
    }
}