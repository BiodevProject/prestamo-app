package com.biovizion.prestamo911.controller;


import java.util.List;

import com.biovizion.prestamo911.service.UsuarioService;
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

import com.biovizion.prestamo911.entities.FacturaEntity;
import com.biovizion.prestamo911.entities.UsuarioEntity;
import com.biovizion.prestamo911.service.FacturaService;

@Controller
@RequestMapping("/factura")
public class FacturaController {
    @Autowired
    private FacturaService facturaService;

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping("/form")
    public String FacturaForm() {

        return "factura/facturaForm";
    }

    @PostMapping("/save")
    public String saveUsuario(@ModelAttribute FacturaEntity factura, Model model) {
        if (facturaService.findById(factura.getId()).isPresent()) {
            model.addAttribute("error", "Esta factura ya existe");
            return "factura/facturaForm";
        }

        facturaService.save(factura);
        return "redirect:/factura/dashboard";
    }


    @GetMapping("/dashboard")
    public String facturaDashboard(Model model) {
        List<FacturaEntity> facturas = facturaService.findAll();
        model.addAttribute("facturas", facturas);

        return "factura/facturaDashboard";
    }
    @GetMapping("/edit/{id}")
    public String facturaEdit(@PathVariable Long id, Model model){
        FacturaEntity factura = facturaService.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        model.addAttribute("factura", factura);
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