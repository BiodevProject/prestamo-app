package com.biovizion.prestamo911.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import com.biovizion.prestamo911.entities.UsuarioEntity;
import com.biovizion.prestamo911.entities.TrabajadorEntity;
import com.biovizion.prestamo911.entities.CreditoEntity;
import com.biovizion.prestamo911.entities.CreditoCuotaEntity;
import com.biovizion.prestamo911.service.UsuarioService;
import com.biovizion.prestamo911.service.TrabajadorService;
import com.biovizion.prestamo911.service.CreditoService;
import com.biovizion.prestamo911.service.CreditoCuotaService;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UsuarioService usuarioService;
    @Autowired
    private TrabajadorService trabajadorService;
    @Autowired
    private CreditoService creditoService;
    @Autowired
    private CreditoCuotaService creditoCuotaService;

    @GetMapping("/creditos/cobros")
    public String showCobros(Model model) {
        // Get all cuotas with different statuses for the tabs
        List<CreditoCuotaEntity> cuotasEnRevision = creditoCuotaService.findEnRevision();
        List<CreditoCuotaEntity> cuotasAvencer = creditoCuotaService.findAvencer();
        List<CreditoCuotaEntity> cuotasVencidas = creditoCuotaService.findVencidas();
        
        model.addAttribute("cuotas", cuotasEnRevision);
        model.addAttribute("cuotasAvencer", cuotasAvencer);
        model.addAttribute("cuotasVencidas", cuotasVencidas);
        return "appDashboard/admin/creditosCobros";
    }

    @GetMapping("/usuarios/{usuarioId}/creditos")
    public String showUsuarioCreditos(@PathVariable Long usuarioId, Model model) {
        // Get the user
        UsuarioEntity usuario = usuarioService.findById(usuarioId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));
        
        // Get all credits for this user
        List<CreditoEntity> creditos = creditoService.findByUsuarioId(usuarioId);
        
        model.addAttribute("usuario", usuario);
        model.addAttribute("creditos", creditos);
        return "appDashboard/admin/usuarioCreditos";
    }

    @PostMapping("/usuarios/update")
    @ResponseBody
    public String updateUsuario(@ModelAttribute UsuarioEntity usuario) {
        try {
            // Obtener el usuario existente y conservar la contraseña
            UsuarioEntity usuarioExistente = usuarioService.findById(usuario.getId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

            usuario.setPassword(usuarioExistente.getPassword());

            usuarioService.update(usuario);
            return "success";
        } catch (Exception e) {
            return "error: " + e.getMessage();
        }
    }

    @PostMapping("/trabajadores/update")
    @ResponseBody
    public String updateTrabajador(@ModelAttribute TrabajadorEntity trabajador) {
        try {
            // Obtener el trabajador existente y conservar la contraseña
            TrabajadorEntity trabajadorExistente = trabajadorService.findById(trabajador.getId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

            trabajador.setPassword(trabajadorExistente.getPassword());

            trabajadorService.update(trabajador);
            return "success";
        } catch (Exception e) {
            return "error: " + e.getMessage();
        }
    }
} 