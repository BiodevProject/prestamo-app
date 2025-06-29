package com.biovizion.prestamo911.controller;

import com.biovizion.prestamo911.entities.CreditoEntity;
import com.biovizion.prestamo911.service.CreditoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import com.biovizion.prestamo911.entities.UsuarioEntity;
import com.biovizion.prestamo911.entities.UsuarioSolicitudEntity;
import com.biovizion.prestamo911.service.UsuarioService;
import com.biovizion.prestamo911.service.UsuarioSolicitudService;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/usuario")
public class UsuarioController {

    @Autowired
    private UsuarioSolicitudService usuarioSolicitudService;
    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private CreditoService creditoService;


    
    @GetMapping("/create")
    public String createUser(Model model) {
        model.addAttribute("usuario", new UsuarioEntity());
        return "auth/register";
    }
    
    @GetMapping("/dashboard")
    public String ShowListaUsuarios(Model model, Principal principal) {
        // Get current user's name
        String currentUserName = getCurrentUserName(principal);
        model.addAttribute("currentUserName", currentUserName);
        
        List<UsuarioEntity> usuarios = usuarioService.findAll();
        model.addAttribute("usuarios", usuarios);
        return "appDashboard/user/index";
    }

    @GetMapping("/solicitarCredito")
    public String solicitarCreditoForm(Model model, Principal principal) {
        // Get current user's name
        String currentUserName = getCurrentUserName(principal);
        model.addAttribute("currentUserName", currentUserName);
        model.addAttribute("credito", new com.biovizion.prestamo911.entities.CreditoEntity());
        return "appDashboard/user/solicitarCredito";
    }
    
    @GetMapping("/estadoDeCreditos")
    public String estadoDeCreditos(Model model, Principal principal) {
        // Get current user's name
        String currentUserName = getCurrentUserName(principal);
        model.addAttribute("currentUserName", currentUserName);
        
        String emailUsuario = principal.getName();
        UsuarioEntity usuario = usuarioService.findByEmail(emailUsuario)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        List<CreditoEntity> creditos = creditoService.findByUsuarioId(usuario.getId());
        model.addAttribute("creditos", creditos);
        return "appDashboard/user/creditosPendientes";
    }

    @GetMapping("/pagarCredito")
    public String pagarCredito(Model model, Principal principal) {
        // Get current user's name
        String currentUserName = getCurrentUserName(principal);
        model.addAttribute("currentUserName", currentUserName);

        String emailUsuario = principal.getName();
        UsuarioEntity usuario = usuarioService.findByEmail(emailUsuario)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        List<CreditoEntity> creditos = creditoService.findByUsuarioId(usuario.getId());
        model.addAttribute("creditos", creditos);
        return "appDashboard/user/pagarCredito";
    }

    @GetMapping("/creditos/detalle/{id}/modal")
    public String creditoDetalleModal(@PathVariable Long id, Model model, Principal principal) {
        String emailUsuario = principal.getName();
        UsuarioEntity usuario = usuarioService.findByEmail(emailUsuario)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        CreditoEntity credito = creditoService.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        
        // Verify that the credit belongs to the current user
        if (!credito.getUsuario().getId().equals(usuario.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No tienes permiso para ver este crédito");
        }
        
        model.addAttribute("credito", credito);
        return "appDashboard/user/creditoDetalleModal";
    }
    
    @GetMapping("/cuenta")
    public String redirectToUserEdit(Principal principal) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();

        UsuarioEntity usuario = usuarioService.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con email: " + email));

        // Aquí rediriges al panel real que muestra los datos personales
        return "redirect:/usuario/edit/" + usuario.getId();
    }

    @GetMapping("/edit/{id}")
    public String editUsuario(@PathVariable Long id, Model model, Principal principal) {
        // Get current user's name
        String currentUserName = getCurrentUserName(principal);
        model.addAttribute("currentUserName", currentUserName);
        
        UsuarioEntity usuario = usuarioService.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        model.addAttribute("usuario", usuario);
        return "usuario/userEdit";
    }

    @PostMapping("/save")
    public String saveUsuario(@ModelAttribute UsuarioEntity usuario, Model model) {
        System.out.println("Guardando usuario: " + usuario.getEmail());
        if (usuarioService.findByEmail(usuario.getEmail()).isPresent()) {
            model.addAttribute("error", "El email ya está registrado");
            return "auth/registro";
        }

        // Generar código único
        String codigo;
        int intentos = 0;
        int maxIntentos = 10; // para evitar bucle infinito
        do {
            codigo = generarCodigo(usuario.getNombre(), usuario.getApellido());
            intentos++;
            if (intentos > maxIntentos) {
                model.addAttribute("error", "No se pudo generar un código único, intente más tarde");
                return "auth/registro";
            }
        } while (usuarioService.existsByCodigo(codigo));  // Validar que no exista

        usuario.setCodigo(codigo);

        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        usuario.setRol("USER");

        usuarioService.save(usuario);
        return "redirect:/auth/login";
    }

    private String generarCodigo(String nombre, String apellido) {
        nombre = nombre.length() >= 2 ? nombre.substring(0, 2).toUpperCase() : nombre.toUpperCase();
        apellido = apellido.length() >= 1 ? apellido.substring(0, 1).toUpperCase() : "";
        int randomNum = (int)(Math.random() * 900000) + 100000; // 6 números aleatorios entre 100000 y 999999
        return nombre + apellido + randomNum;
    }



    @PostMapping("/update")
    public String updateUsuario(@ModelAttribute UsuarioEntity usuario) {

        // Obtener el usuario existente y conservar la contraseña
        UsuarioEntity usuarioExistente = usuarioService.findById(usuario.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        usuario.setPassword(usuarioExistente.getPassword());

        usuarioService.update(usuario);
        return "redirect:/usuario/dashboard";
    }

    @PostMapping("/delete/{id}")
    public String deleteUsuario(@PathVariable Long id) {
        usuarioService.delete(id);
        return "redirect:/usuario/dashboard";
    }

    // Helper method to get current user's name
    private String getCurrentUserName(Principal principal) {
        if (principal == null) {
            return "Usuario";
        }
        
        try {
            String email = principal.getName();
            UsuarioEntity usuario = usuarioService.findByEmail(email)
                    .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
            return usuario.getNombre() != null ? usuario.getNombre() : email;
        } catch (Exception e) {
            return principal.getName();
        }
    }
}
