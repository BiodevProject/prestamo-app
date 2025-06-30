package com.biovizion.prestamo911.controller;


import com.biovizion.prestamo911.entities.AlertaEntity;
import com.biovizion.prestamo911.entities.UsuarioEntity;
import com.biovizion.prestamo911.service.AlertaService;
import com.biovizion.prestamo911.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("")
public class AlertaController {

    @Autowired
    private AlertaService alertaService;

    @GetMapping("/alertas/{id}")
    @ResponseBody
    public ResponseEntity<?> getAlertaPorId(@PathVariable Long id) {
        try {
            return alertaService.findById(id)
                    .map(alerta -> ResponseEntity.ok(Map.of(
                            "mensaje", alerta.getMensaje(),
                            "codigo", alerta.getCodigo(),
                            "tipo", alerta.getTipo() // Este campo debe existir en AlertaEntity
                    )))
                    .orElse(ResponseEntity.ok(Map.of(
                            "mensaje", "No hay alertas activas",
                            "codigo", "INFO",
                            "tipo", "info"
                    )));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "mensaje", "Error al obtener la alerta",
                            "codigo", "ERROR",
                            "tipo", "danger"
                    ));
        }
    }
}
