package com.biovizion.prestamo911;

import com.biovizion.prestamo911.entities.CreditoCuotaEntity;
import com.biovizion.prestamo911.entities.CreditoEntity;
import com.biovizion.prestamo911.entities.UsuarioEntity;
import com.biovizion.prestamo911.service.AlertaService;
import com.biovizion.prestamo911.service.CreditoCuotaService;
import com.biovizion.prestamo911.service.CreditoService;
import com.biovizion.prestamo911.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Component
@ControllerAdvice
public class GlobalAlertas {

    @Autowired
    private AlertaService alertaService;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private CreditoService creditoService;

    @Autowired
    private CreditoCuotaService creditoCuotaService;

    @ModelAttribute
    public void addSelectedAlerts(Model model, Principal principal) {
        // alertas fijas
        model.addAttribute("alerta1", alertaService.findById(1L).orElse(null));
        model.addAttribute("alerta2", alertaService.findById(2L).orElse(null));
        model.addAttribute("alerta5", alertaService.findById(3L).orElse(null));
        model.addAttribute("alerta4", alertaService.findById(4L).orElse(null));
        model.addAttribute("alerta6", alertaService.findById(6L).orElse(null));
        model.addAttribute("alerta7", alertaService.findById(7L).orElse(null));
        model.addAttribute("alerta8", alertaService.findById(8L).orElse(null));
        model.addAttribute("alerta9", alertaService.findById(9L).orElse(null));
        model.addAttribute("alerta10", alertaService.findById(10L).orElse(null));

        if (principal != null) {
            Optional<UsuarioEntity> optionalUsuario = usuarioService.findByEmail(principal.getName());
            if (optionalUsuario.isPresent()) {
                UsuarioEntity usuario = optionalUsuario.get();

                // Ahora podés hacer llamadas con diferentes estados según la alerta que quieras validar
                List<CreditoCuotaEntity> cuotasVencidas = creditoCuotaService.getCuotasByUsuarioAndEstado(usuario.getId(), "vencido");
                if (!cuotasVencidas.isEmpty()) {
                    model.addAttribute("PAGO_RETRASADO", alertaService.findById(3L).orElse(null));
                }

                List<CreditoCuotaEntity> cuotasPagadas = creditoCuotaService.getCuotasByUsuarioAndEstado(usuario.getId(), "EnRevision");
                if (!cuotasPagadas.isEmpty()) {
                    model.addAttribute("PAGO_EN_REVISION", alertaService.findById(5L).orElse(null));
                }

                model.addAttribute("alerta5", alertaService.findById(5L).orElse(null));
                model.addAttribute("alerta5", alertaService.findById(5L).orElse(null));



                // Y así para las demás alertas...
            }
        }

    }
    }
