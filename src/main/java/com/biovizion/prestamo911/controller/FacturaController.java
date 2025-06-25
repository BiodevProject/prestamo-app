package com.biovizion.prestamo911.controller;


import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

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

import com.biovizion.prestamo911.entities.DetalleFacturaEntity;
import com.biovizion.prestamo911.entities.FacturaEntity;
import com.biovizion.prestamo911.entities.ProductoEntity;
import com.biovizion.prestamo911.entities.UsuarioEntity;
import com.biovizion.prestamo911.service.DetalleFacturaService;
import com.biovizion.prestamo911.service.FacturaService;
import com.biovizion.prestamo911.service.ProductoService;
import com.biovizion.prestamo911.service.UsuarioService;

@Controller
@RequestMapping("/factura")
public class FacturaController {
    @Autowired
    private FacturaService facturaService;
    @Autowired
    private DetalleFacturaService facturaDetalleService;

    @Autowired
    private UsuarioService usuarioService;
    @Autowired
    private ProductoService productoService;

    @GetMapping("/form")
    public String FacturaForm(Model model) {
        List<UsuarioEntity> usuarios = usuarioService.findAll();
        List<ProductoEntity> productos = productoService.findAll();

        model.addAttribute("usuarios", usuarios);
        model.addAttribute("productos", productos);
        return "factura/facturaForm";
    }

    @PostMapping("/save")
    public String saveFactura(@ModelAttribute FacturaEntity factura, Model model) {
        if (factura.getDetalles() != null) {
            for (DetalleFacturaEntity detalle : factura.getDetalles()) {
                detalle.setFactura(factura);  // set back-reference!
            }
        }
        facturaService.save(factura);
        return "redirect:/factura/dashboard";
    }

    @GetMapping("/dashboard")
    public String facturaDashboard(Model model) {
        List<FacturaEntity> facturas = facturaService.findAll();
        model.addAttribute("facturas", facturas);

        for (FacturaEntity factura : facturas) {
            System.out.println(factura.getFechaEmision());
        }

        return "factura/facturaDashboard";
    }

    @GetMapping("/edit/{id}")
    public String facturaEdit(@PathVariable Long id, Model model){
        FacturaEntity factura = facturaService.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        // Get the detalles list from the factura entity
        List<DetalleFacturaEntity> detalles = factura.getDetalles();

        // Add both factura and detalles to the model
        model.addAttribute("factura", factura);
        model.addAttribute("detalles", detalles);

        return "factura/facturaEdit";
    }

    @PostMapping("/update")
    public String facturaUpdate(@ModelAttribute FacturaEntity factura) {
        facturaService.update(factura);
        return "redirect:/factura/edit/" + factura.getId();
    }

    @PostMapping("/delete/{id}")
    public String deleteFactura(@PathVariable Long id) {
        facturaService.delete(id);
        return "redirect:/factura/dashboard";
    }
}