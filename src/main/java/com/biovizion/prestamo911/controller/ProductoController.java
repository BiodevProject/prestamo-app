package com.biovizion.prestamo911.controller;

import java.util.List;

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

import com.biovizion.prestamo911.entities.ProductoEntity;
import com.biovizion.prestamo911.service.ProductoService;

@Controller
@RequestMapping("/producto")
public class ProductoController {
    @Autowired
    private ProductoService productoService;

    @GetMapping("/form")
    public String ProductoForm(Model model) {
        return "producto/productoForm";
    }

    @PostMapping("/save")
    public String saveProducto(@ModelAttribute ProductoEntity producto, Model model) {
        if (productoService.findByNombre(producto.getNombre()).isPresent()) {
            model.addAttribute("error", "Esta producto ya existe");
            return "producto/productoForm";
        }

        productoService.save(producto);
        return "redirect:/producto/dashboard";
    }


    @GetMapping("/dashboard")
    public String productoDashboard(Model model) {
        List<ProductoEntity> productos = productoService.findAll();
        model.addAttribute("productos", productos);

        return "producto/productoDashboard";
    }
    @GetMapping("/edit/{id}")
    public String productoEdit(@PathVariable Long id, Model model){
        ProductoEntity producto = productoService.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        model.addAttribute("producto", producto);
        return "producto/productoEdit";
    }
    @PostMapping("/update")
    public String productoUpdate(@ModelAttribute ProductoEntity producto) {
        productoService.update(producto);
        return "redirect:/producto/edit/" + producto.getId();
    }

    @PostMapping("/delete/{id}")
    public String productoDelete(@PathVariable Long id) {
        productoService.delete(id);
        return "redirect:/producto/dashboard";
    }
}