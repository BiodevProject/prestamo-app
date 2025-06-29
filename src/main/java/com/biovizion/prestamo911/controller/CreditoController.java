package com.biovizion.prestamo911.controller;

import java.time.LocalDate;
import java.util.List;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.format.annotation.DateTimeFormat;

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
        
        credito.setEstado("Aceptado");
        credito.setFechaAceptado(LocalDateTime.now());
        creditoService.update(credito);
        
        return "redirect:/admin/creditos/pendientes";
    }

    @PostMapping("/accept-with-charges")
    public String acceptWithCharges(@RequestParam("creditoId") Long creditoId,
                                    @RequestParam("porcentajeInteres") BigDecimal porcentajeInteres,
                                    @RequestParam("porcentajeMora") BigDecimal porcentajeMora,
                                    @RequestParam("porcentajeIva") BigDecimal porcentajeIva,
                                    @RequestParam("comisionFija") BigDecimal comisionFija,
                                    @RequestParam("proximoPago") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate proximoPago) {

        CreditoEntity credito = creditoService.findById(creditoId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Credito not found"));

        // Set admin-configurable values
        credito.setPorcentajeInteres(porcentajeInteres);
        credito.setPorcentajeMora(porcentajeMora);
        credito.setPorcentajeIva(porcentajeIva);
        credito.setComisionFija(comisionFija);

        // Guardar fecha de próximo pago
        credito.setProximoPago(proximoPago);

        // Resto de cálculos y guardado
        BigDecimal monto = credito.getMonto();
        int plazoEnMeses = credito.getPlazoMeses();

        BigDecimal cien = new BigDecimal("100");
        BigDecimal doce = new BigDecimal("12");

        BigDecimal tasaMensual = porcentajeInteres
                .divide(cien, 10, RoundingMode.HALF_UP)
                .divide(doce, 10, RoundingMode.HALF_UP);

        BigDecimal unoMasTasa = BigDecimal.ONE.add(tasaMensual);
        BigDecimal potencia = unoMasTasa.pow(plazoEnMeses);

        BigDecimal cuotaMensual = monto.multiply(tasaMensual).multiply(potencia)
                .divide(potencia.subtract(BigDecimal.ONE), 2, RoundingMode.HALF_UP);

        BigDecimal totalCuotas = cuotaMensual.multiply(new BigDecimal(plazoEnMeses));
        BigDecimal subtotal = totalCuotas.add(comisionFija);

        BigDecimal iva = subtotal.multiply(porcentajeIva.divide(cien, 4, RoundingMode.HALF_UP));
        BigDecimal total = subtotal.add(iva);

        credito.setInteres(totalCuotas.subtract(monto).setScale(2, RoundingMode.HALF_UP));
        credito.setMora(BigDecimal.ZERO);
        credito.setIva(iva.setScale(2, RoundingMode.HALF_UP));
        credito.setTotal(total.setScale(2, RoundingMode.HALF_UP));
        credito.setCuotaMensual(cuotaMensual.setScale(2, RoundingMode.HALF_UP));

        credito.setEstado("Aceptado");
        credito.setFechaAceptado(LocalDateTime.now());

        creditoService.save(credito);

        return "redirect:/admin/creditos/pendientes";
    }


    @PostMapping("/pagar/{id}")
    public String realizarPago(@PathVariable Long id, @RequestParam("monto") BigDecimal montoPago) {
        CreditoEntity credito = creditoService.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Credito not found"));
        
        // Subtract payment amount from total
        BigDecimal nuevoTotal = credito.getTotal().subtract(montoPago);
        credito.setTotal(nuevoTotal.setScale(2, RoundingMode.HALF_UP));
        
        // If total reaches zero or below, mark as finalized
        if (nuevoTotal.compareTo(BigDecimal.ZERO) <= 0) {
            credito.setEstado("Finalizado");
        }
        
        creditoService.save(credito);
        
        return "redirect:/usuario/estadoDeCreditos";
    }
}