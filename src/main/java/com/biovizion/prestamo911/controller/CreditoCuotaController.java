package com.biovizion.prestamo911.controller;

import com.biovizion.prestamo911.entities.CreditoCuotaEntity;
import com.biovizion.prestamo911.entities.CreditoEntity;
import com.biovizion.prestamo911.service.CreditoCuotaService;
import com.biovizion.prestamo911.service.CreditoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

@Controller
@RequestMapping("/cuota")
public class CreditoCuotaController {

    @Autowired
    private CreditoCuotaService creditoCuotaService;

    @Autowired
    private CreditoService creditoService;

    @PostMapping("/pagar/{id}")
    public String pagarCuota(@PathVariable Long id, @RequestParam("monto") BigDecimal montoPago) {
        try {
            // Find the cuota
            CreditoCuotaEntity cuota = creditoCuotaService.findById(id)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cuota not found"));

            // Get the associated credito
            CreditoEntity credito = cuota.getCredito();

            // Subtract the cuota amount from the credito total
            BigDecimal nuevoTotal = credito.getTotal().subtract(cuota.getMonto());
            credito.setTotal(nuevoTotal.setScale(2, RoundingMode.HALF_UP));

            // If total reaches zero or below, mark as finalized
            if (nuevoTotal.compareTo(BigDecimal.ZERO) <= 0) {
                credito.setEstado("Finalizado");
            }

            // Update the cuota status to "Pagado"
            cuota.setEstado("Pagado");
            cuota.setFechaPago(LocalDateTime.now());
            creditoCuotaService.save(cuota);

            // Save the updated credito
            creditoService.save(credito);

            return "redirect:/usuario/pagarCredito";
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error processing payment", e);
        }
    }
} 