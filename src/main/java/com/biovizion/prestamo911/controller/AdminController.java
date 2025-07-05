package com.biovizion.prestamo911.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
import java.security.Principal;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

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


    // < === Paginas de Admin === >

    @GetMapping("/dashboard")
    public String showDashboard(Model model, Principal principal) {
        try {
            if (principal != null) {
                UsuarioEntity usuario = usuarioService.findByEmail(principal.getName())
                    .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
                model.addAttribute("usuario", usuario);
            }
        } catch (Exception e) {
            // Handle exception if user not found
        }
        
        return "appDashboard/admin/index";
    }
    
    @GetMapping("/creditos")
    public String showCreditos(Model model) {
        List<CreditoEntity> creditos = creditoService.findAll();
        List<CreditoEntity> creditosPendientes = creditoService.findPendientes();
        List<CreditoEntity> creditosAceptados = creditoService.findAceptados();
        List<CreditoEntity> creditosRechazados = creditoService.findRechazados();
        List<CreditoEntity> creditosFinalizados = creditoService.findFinalizados();
        
        model.addAttribute("creditos", creditos);
        model.addAttribute("creditosPendientes", creditosPendientes);
        model.addAttribute("creditosAceptados", creditosAceptados);
        model.addAttribute("creditosRechazados", creditosRechazados);   
        model.addAttribute("creditosFinalizados", creditosFinalizados);
        
        return "appDashboard/admin/creditos";
    }
    
    @GetMapping("/creditos/cobros")
    public String showCobros(Model model) {
        List<CreditoCuotaEntity> cuotas = creditoCuotaService.findAll();
        List<CreditoCuotaEntity> cuotasPendientes = creditoCuotaService.findPendientes();
        List<CreditoCuotaEntity> cuotasEnRevision = creditoCuotaService.findEnRevision();
        List<CreditoCuotaEntity> cuotasAvencer = creditoCuotaService.findAVencer();
        List<CreditoCuotaEntity> cuotasVencidas = creditoCuotaService.findVencidas();
        List<CreditoCuotaEntity> cuotasPagadas = creditoCuotaService.findPagadas();

        model.addAttribute("cuotas", cuotas);
        model.addAttribute("cuotasPendientes", cuotasPendientes);
        model.addAttribute("cuotasEnRevision", cuotasEnRevision);
        model.addAttribute("cuotasAvencer", cuotasAvencer);
        model.addAttribute("cuotasVencidas", cuotasVencidas);
        model.addAttribute("cuotasPagadas", cuotasPagadas);
        
        return "appDashboard/admin/creditosCobros";
    }

    @PostMapping("/creditos/{id}/aceptar")
    public ResponseEntity<String> aceptarCredito(@PathVariable Long id) {
        try {
            CreditoEntity credito = creditoService.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
            
            // Update the credit status to "Aceptado"
            credito.setEstado("Aceptado");
            creditoService.update(credito);
            
            return ResponseEntity.ok("Crédito aceptado exitosamente");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error al aceptar el crédito: " + e.getMessage());
        }
    }
    
    @PostMapping("/creditos/{id}/rechazar")
    public ResponseEntity<String> rechazarCredito(@PathVariable Long id) {
        try {
            CreditoEntity credito = creditoService.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
            
            // Update the credit status to "Rechazado"
            credito.setEstado("Rechazado");
            creditoService.update(credito);
            
            return ResponseEntity.ok("Crédito rechazado exitosamente");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error al rechazar el crédito: " + e.getMessage());
        }
    }

    @GetMapping("/usuarios")
    public String showUsuarios(Model model) {
        List<UsuarioEntity> usuarios = usuarioService.findAll();
        model.addAttribute("usuarios", usuarios);
        return "appDashboard/admin/usuarios";
    }
    
    @GetMapping("/usuarios/{usuarioId}/creditos")
    public String sendToUsuarioCreditos(Model model, @PathVariable Long usuarioId) {
        List<CreditoEntity> creditos = creditoService.findByUsuarioId(usuarioId);
        List<CreditoEntity> creditosAceptados = creditoService.findAceptadosByUsuarioId(usuarioId);
        List<CreditoEntity> creditosPendientes = creditoService.findPendientesByUsuarioId(usuarioId);
        List<CreditoEntity> creditosRechazados = creditoService.findRechazadosByUsuarioId(usuarioId);
        List<CreditoEntity> creditosFinalizados = creditoService.findFinalizadosByUsuarioId(usuarioId);

        model.addAttribute("creditos", creditos);
        model.addAttribute("creditosAceptados", creditosAceptados);
        model.addAttribute("creditosPendientes", creditosPendientes);
        model.addAttribute("creditosRechazados", creditosRechazados);
        model.addAttribute("creditosFinalizados", creditosFinalizados);

        return "appDashboard/admin/usuarioCreditos";
    }

    @GetMapping("/usuarios/{usuarioId}/creditos/{creditoId}/cuotas")
    public String sendToUsuarioCreditoCuotas(Model model, @PathVariable Long usuarioId, @PathVariable Long creditoId) {
        List<CreditoCuotaEntity> cuotas = creditoCuotaService.findByCreditoId(creditoId);
        List<CreditoCuotaEntity> cuotasPendientes = creditoCuotaService.findPendientesByCreditoId(creditoId);
        List<CreditoCuotaEntity> cuotasEnRevision = creditoCuotaService.findEnRevisionByCreditoId(creditoId);
        List<CreditoCuotaEntity> cuotasPagadas = creditoCuotaService.findPagadasByCreditoId(creditoId);
        List<CreditoCuotaEntity> cuotasVencidas = creditoCuotaService.findVencidasByCreditoId(creditoId);
        List<CreditoCuotaEntity> cuotasAvencer = creditoCuotaService.findAVencerByCreditoId(creditoId);

        model.addAttribute("cuotas", cuotas);
        model.addAttribute("cuotasPendientes", cuotasPendientes);
        model.addAttribute("cuotasEnRevision", cuotasEnRevision);
        model.addAttribute("cuotasPagadas", cuotasPagadas);
        model.addAttribute("cuotasVencidas", cuotasVencidas);
        model.addAttribute("cuotasAvencer", cuotasAvencer);

        return "appDashboard/admin/usuarioCreditoCuotas";
    }

    @PostMapping("/usuarios/update")
    public ResponseEntity<String> updateUsuario(@ModelAttribute UsuarioEntity usuario) {
        try {
            // Obtener el usuario existente y conservar la contraseña
            UsuarioEntity usuarioExistente = usuarioService.findById(usuario.getId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

            usuario.setPassword(usuarioExistente.getPassword());

            usuarioService.update(usuario);
            return ResponseEntity.ok("Usuario actualizado exitosamente");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error al actualizar el usuario: " + e.getMessage());
        }
    }

    @PostMapping("/trabajadores/update")
    public ResponseEntity<String> updateTrabajador(@ModelAttribute TrabajadorEntity trabajador) {
        try {
            // Obtener el trabajador existente y conservar la contraseña
            TrabajadorEntity trabajadorExistente = trabajadorService.findById(trabajador.getId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

            trabajador.setPassword(trabajadorExistente.getPassword());

            trabajadorService.update(trabajador);
            return ResponseEntity.ok("Trabajador actualizado exitosamente");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error al actualizar el trabajador: " + e.getMessage());
        }
    }
} 