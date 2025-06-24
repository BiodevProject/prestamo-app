package com.biovizion.prestamo911.controller;


import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/factura")
public class FacturaController {


    @GetMapping("/dashboard")
    public String facturaDashboard(Model model) {




        return "factura/facturaDashboard";

    }

    @GetMapping("/form")
    public String FacturaForm() {
        return "factura/facturaForm";
    }


}
