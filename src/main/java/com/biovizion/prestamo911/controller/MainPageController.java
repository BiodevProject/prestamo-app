package com.biovizion.prestamo911.controller;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("")
public class MainPageController {
    @GetMapping("/")
    public String SelectoOption() {
        return "/mainPage/index";
    }

    @GetMapping("/about")
    public String mostrarAbout() {
        return "/mainPage/about";
    }

    @GetMapping("/blog")
    public String mostrarBlog() {
        return "/mainPage/blog";
    }

    @GetMapping("/contact")
    public String mostrarContact() {
        return "/mainPage/contact";
    }

    @GetMapping("/service")
    public String mostrarService() {
        return "/mainPage/service";
    }

}
