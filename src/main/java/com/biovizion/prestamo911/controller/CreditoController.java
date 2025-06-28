package com.biovizion.prestamo911.controller;

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
                                    @RequestParam("comisionFija") BigDecimal comisionFija) {
    
        CreditoEntity credito = creditoService.findById(creditoId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Credito not found"));
    
        // Set admin-configurable values
        credito.setPorcentajeInteres(porcentajeInteres);
        credito.setPorcentajeMora(porcentajeMora);
        credito.setPorcentajeIva(porcentajeIva);
        credito.setComisionFija(comisionFija);
    
        // Perform calculations
        BigDecimal monto = credito.getMonto();
        int plazoEnMeses = credito.getPlazoMeses();
    
        BigDecimal cien = new BigDecimal("100");
        BigDecimal doce = new BigDecimal("12");
    
        // Calculate monthly interest rate
        BigDecimal tasaMensual = porcentajeInteres
            .divide(cien, 10, RoundingMode.HALF_UP)
            .divide(doce, 10, RoundingMode.HALF_UP);
    
        // (1 + r)^n
        BigDecimal unoMasTasa = BigDecimal.ONE.add(tasaMensual);
        BigDecimal potencia = unoMasTasa.pow(plazoEnMeses);
    
        // Amortization formula: cuota = P * [ r * (1 + r)^n ] / [ (1 + r)^n - 1 ]
        BigDecimal cuotaMensual = monto.multiply(tasaMensual).multiply(potencia)
            .divide(potencia.subtract(BigDecimal.ONE), 2, RoundingMode.HALF_UP);
    
        // Total repayment = cuota mensual * meses + fixed commission + IVA
        BigDecimal totalCuotas = cuotaMensual.multiply(new BigDecimal(plazoEnMeses));
        BigDecimal subtotal = totalCuotas.add(comisionFija);
    
        BigDecimal iva = subtotal.multiply(porcentajeIva.divide(cien, 4, RoundingMode.HALF_UP));
        BigDecimal total = subtotal.add(iva);
    
        // Set calculated values
        credito.setInteres(totalCuotas.subtract(monto).setScale(2, RoundingMode.HALF_UP)); // interest only
        credito.setMora(BigDecimal.ZERO); // No mora at start
        credito.setIva(iva.setScale(2, RoundingMode.HALF_UP));
        credito.setTotal(total.setScale(2, RoundingMode.HALF_UP));
    
        // You can also save cuotaMensual if needed
        credito.setCuotaMensual(cuotaMensual.setScale(2, RoundingMode.HALF_UP)); // ← if your entity has this
    
        // Update estado and fecha
        credito.setEstado("Aceptado");
        credito.setFechaAceptado(java.time.LocalDateTime.now());
    
        creditoService.save(credito);
    
        return "redirect:/admin/creditos/pendientes";
    }    
}