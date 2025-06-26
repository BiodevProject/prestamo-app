package com.biovizion.prestamo911.controller;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("")
public class HomeController {


    @GetMapping("/home")
    public String mostrarIndex() {
        return "/home/home";
    }

    @GetMapping("/")
    public String SelectoOption() {
        return "/home/landingPage";
    }

}
