package com.biovizion.prestamo911.controller;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.UUID;

import com.biovizion.prestamo911.entities.UsuarioEntity;
import com.biovizion.prestamo911.service.PdfService;
import com.biovizion.prestamo911.service.UsuarioService;
import jakarta.servlet.http.HttpServletResponse;
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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.format.annotation.DateTimeFormat;

import com.biovizion.prestamo911.entities.CreditoCuotaEntity;
import com.biovizion.prestamo911.entities.CreditoEntity;
import com.biovizion.prestamo911.entities.UsuarioSolicitudEntity;
import com.biovizion.prestamo911.service.CreditoCuotaService;
import com.biovizion.prestamo911.service.CreditoService;

@Controller
@RequestMapping("/credito")
public class CreditoController {
    @Autowired
    private CreditoService creditoService;
    @Autowired
    private CreditoCuotaService creditoCuotaService;

    @Autowired
    private UsuarioService usuarioService;
    @Autowired
    private PdfService pdfService;


    @PostMapping("/pagar/{id}")
    public void pagarCuotaYGenerarPDF(@PathVariable Long id,
                                      @RequestParam("monto") BigDecimal montoPago,
                                      HttpServletResponse response) {
        try {
            CreditoCuotaEntity cuota = creditoCuotaService.findById(id)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cuota no encontrada"));

            CreditoEntity credito = cuota.getCredito();

            BigDecimal nuevoTotal = credito.getTotal().subtract(cuota.getMonto());
            credito.setTotal(nuevoTotal.setScale(2, RoundingMode.HALF_UP));

            if (nuevoTotal.compareTo(BigDecimal.ZERO) <= 0) {
                credito.setEstado("Finalizado");
            }

            cuota.setEstado("EnRevision");
            cuota.setFechaPago(LocalDateTime.now());
            creditoCuotaService.save(cuota);

            creditoService.save(credito);

            // Generar y devolver PDF
            pdfService.generarFacturaPDF(cuota, response);

        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error procesando pago", e);
        }
    }


    @GetMapping("/form")
    public String creditoForm(Model model) {
        CreditoEntity credito = new CreditoEntity();
        credito.setUsuarioSolicitud(new UsuarioSolicitudEntity()); // initialize nested object

        model.addAttribute("credito", credito);
        return "credito/creditoForm";
    }

    @PostMapping("/save")
    public String saveCredito(@ModelAttribute CreditoEntity credito,
                              @RequestParam(value = "duiDelante", required = false) MultipartFile duiDelanteFile,
                              @RequestParam(value = "duiAtras", required = false) MultipartFile duiAtrasFile) throws IOException {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();

        UsuarioEntity usuario = usuarioService.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        String rutaFotos = "/home/user1/Documents/fotos/";

        if (duiDelanteFile != null && !duiDelanteFile.isEmpty()) {
            String nombreArchivoDelante = UUID.randomUUID() + "_" + duiDelanteFile.getOriginalFilename();
            File destinoDelante = new File(rutaFotos + nombreArchivoDelante);
            duiDelanteFile.transferTo(destinoDelante);

            if (credito.getUsuarioSolicitud() != null) {
                credito.getUsuarioSolicitud().setDuiDelante("/fotos-usuarios/" + nombreArchivoDelante);
            }
        }

        if (duiAtrasFile != null && !duiAtrasFile.isEmpty()) {
            String nombreArchivoAtras = UUID.randomUUID() + "_" + duiAtrasFile.getOriginalFilename();
            File destinoAtras = new File(rutaFotos + nombreArchivoAtras);
            duiAtrasFile.transferTo(destinoAtras);

            if (credito.getUsuarioSolicitud() != null) {
                credito.getUsuarioSolicitud().setDuiAtras("/fotos-usuarios/" + nombreArchivoAtras);
            }
        }

        credito.setUsuario(usuario);
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
        List<CreditoEntity> creditos = creditoService.findAceptados();
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

    @PostMapping("/accept-with-charges")
    public String acceptWithCharges(@RequestParam("creditoId") Long creditoId,
                                    @RequestParam("porcentajeInteres") BigDecimal porcentajeInteres,
                                    @RequestParam("porcentajeMora") BigDecimal porcentajeMora,
                                    @RequestParam("porcentajeIva") BigDecimal porcentajeIva,
                                    @RequestParam("comisionFija") BigDecimal comisionFija,
                                    @RequestParam(value = "proximoPago", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate proximoPago) {

        CreditoEntity credito = creditoService.findById(creditoId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Credito not found"));

        // Set admin-configurable values
        credito.setPorcentajeInteres(porcentajeInteres);
        credito.setPorcentajeMora(porcentajeMora);
        credito.setPorcentajeIva(porcentajeIva);
        credito.setComisionFija(comisionFija);

        // Guardar fecha de próximo pago si se proporciona, sino usar fecha actual + 1 mes
        if (proximoPago != null) {
            credito.setProximoPago(proximoPago);
        } else {
            credito.setProximoPago(LocalDate.now().plusMonths(1));
        }

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

        // Calcular monto de mora como porcentaje del total
        BigDecimal mora = total.multiply(porcentajeMora.divide(cien, 4, RoundingMode.HALF_UP));
        System.out.println("MORA CALCULADA: $" + mora); // Debug

        credito.setInteres(totalCuotas.subtract(monto).setScale(2, RoundingMode.HALF_UP));
        credito.setMora(mora.setScale(2, RoundingMode.HALF_UP)); // Mora en dinero
        credito.setIva(iva.setScale(2, RoundingMode.HALF_UP));
        credito.setTotal(total.setScale(2, RoundingMode.HALF_UP));
        credito.setCuotaMensual(cuotaMensual.setScale(2, RoundingMode.HALF_UP));

        credito.setEstado("Aceptado");
        credito.setFechaAceptado(LocalDateTime.now());

        // Crear las cuotas
        for (int i = 0; i < plazoEnMeses; i++) {
            CreditoCuotaEntity cuota = new CreditoCuotaEntity();
            cuota.setCredito(credito);
            cuota.setFechaVencimiento(credito.getFechaAceptado().plusMonths(i));
            cuota.setEstado("Pendiente");
            cuota.setMonto(cuotaMensual);
            creditoCuotaService.save(cuota);
        }

        creditoService.save(credito);

        return "redirect:/admin/creditos";
    }
}