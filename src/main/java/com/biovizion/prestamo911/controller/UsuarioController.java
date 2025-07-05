package com.biovizion.prestamo911.controller;

import com.biovizion.prestamo911.entities.CreditoEntity;
import com.biovizion.prestamo911.entities.CreditoCuotaEntity;
import com.biovizion.prestamo911.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.biovizion.prestamo911.entities.UsuarioEntity;
import com.biovizion.prestamo911.entities.UsuarioSolicitudEntity;

import java.io.File;
import java.io.IOException;
import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.Optional;

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

    @Autowired
    private CreditoCuotaService creditoCuotaService;

    @Autowired
    private EmailService emailService;



    @PostMapping("/save")
    public String saveUsuario(@ModelAttribute UsuarioEntity usuario,
                              @RequestParam("g-recaptcha-response") String captchaResponse,
                              Model model) {
        // 1. Verificar el CAPTCHA
        String secretKey = "6LfdRXQrAAAAAM7a7PFCuYBa5dYLaDeQenArCeAg";  // La clave secreta que te dio Google
        String verifyUrl = "https://www.google.com/recaptcha/api/siteverify";

        RestTemplate restTemplate = new RestTemplate();
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("secret", secretKey);
        params.add("response", captchaResponse);

        ResponseEntity<Map> response = restTemplate.postForEntity(verifyUrl, params, Map.class);
        Map body = response.getBody();

        if (!(Boolean) body.get("success")) {
            model.addAttribute("error", "Por favor valida el captcha.");
            return "auth/registro";
        }

        // 2. Lógica normal si el captcha fue exitoso
        System.out.println("Guardando usuario: " + usuario.getEmail());

        if (usuarioService.findByEmail(usuario.getEmail()).isPresent()) {
            model.addAttribute("error", "El email ya está registrado");
            return "auth/registro";
        }

        String codigo;
        int intentos = 0;
        int maxIntentos = 10;
        do {
            codigo = generarCodigo(usuario.getNombre(), usuario.getApellido());
            intentos++;
            if (intentos > maxIntentos) {
                model.addAttribute("error", "No se pudo generar un código único, intente más tarde");
                return "auth/registro";
            }
        } while (usuarioService.existsByCodigo(codigo));

        usuario.setCodigo(codigo);
        usuario.setActivo(false);
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        usuario.setRol("ROLE_USER");

        usuarioService.save(usuario);
        emailService.enviarCodigoVerificacion(usuario.getEmail(), codigo);

        return "redirect:/verificacion/verificar?email=" + usuario.getEmail();
    }

    private String generarCodigo(String nombre, String apellido) {
        nombre = nombre.length() >= 2 ? nombre.substring(0, 2).toUpperCase() : nombre.toUpperCase();
        apellido = apellido.length() >= 1 ? apellido.substring(0, 1).toUpperCase() : "";
        int randomNum = (int)(Math.random() * 900000) + 100000; // 6 números aleatorios entre 100000 y 999999
        return nombre + apellido + randomNum;
    }



    @GetMapping("/create")
    public String createUser(Model model) {
        model.addAttribute("usuario", new UsuarioEntity());
        return "auth/register";
    }
    
    @GetMapping("/dashboard")
    public String ShowListaUsuarios(Model model, Principal principal) {
        try {
            if (principal != null) {
                UsuarioEntity usuario = usuarioService.findByEmail(principal.getName())
                    .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
                model.addAttribute("usuario", usuario);
            }
        } catch (Exception e) {
            // Handle exception if user not found
        }
        
        return "appDashboard/user/index";
    }

    @GetMapping("/solicitarCredito")
    public String solicitarCreditoForm(Model model, Principal principal) {
        String currentUserName = principal.getName();
        UsuarioEntity usuario = usuarioService.findByEmail(currentUserName)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + currentUserName));

        CreditoEntity credito = new CreditoEntity();
        UsuarioSolicitudEntity solicitud = new UsuarioSolicitudEntity();

        solicitud.setNombres(usuario.getNombre());
        solicitud.setApellidos(usuario.getApellido());
        solicitud.setEmail(usuario.getEmail());

        credito.setUsuarioSolicitud(solicitud);

        model.addAttribute("credito", credito);
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

        // Get all credits and filtered credits by state from service layer
        List<CreditoEntity> creditos = creditoService.findByUsuarioId(usuario.getId());
        List<CreditoEntity> creditosAceptados = creditoService.findAceptadosByUsuarioId(usuario.getId());
        List<CreditoEntity> creditosPendientes = creditoService.findPendientesByUsuarioId(usuario.getId());
        List<CreditoEntity> creditosRechazados = creditoService.findRechazadosByUsuarioId(usuario.getId());
        List<CreditoEntity> creditosFinalizados = creditoService.findFinalizadosByUsuarioId(usuario.getId());

        // Add all credit lists to the model
        model.addAttribute("creditos", creditos);
        model.addAttribute("creditosAceptados", creditosAceptados);
        model.addAttribute("creditosPendientes", creditosPendientes);
        model.addAttribute("creditosRechazados", creditosRechazados);
        model.addAttribute("creditosFinalizados", creditosFinalizados);
        
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

        List<CreditoEntity> creditosAceptados = creditoService.findAceptadosByUsuarioId(usuario.getId());
        
        model.addAttribute("creditosAceptados", creditosAceptados);
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

    @PostMapping("/update")
    public ResponseEntity<String> updateUsuario(
            @RequestParam("id") Long id,
            @RequestParam("nombre") String nombre,
            @RequestParam("email") String email,
            @RequestParam("celular") String celular,
            @RequestParam(value = "foto", required = false) MultipartFile foto,
            Principal principal
    ) {
        UsuarioEntity usuario = usuarioService.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        // Check if this is the current user updating their own profile
        boolean isCurrentUser = principal != null && principal.getName().equals(usuario.getEmail());
        
        // If email is being changed, check if it's already taken by another user
        if (!email.equals(usuario.getEmail())) {
            Optional<UsuarioEntity> existingUser = usuarioService.findByEmail(email);
            if (existingUser.isPresent() && !existingUser.get().getId().equals(id)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El email ya está en uso por otro usuario");
            }
        }

        usuario.setNombre(nombre);
        usuario.setEmail(email);
        usuario.setCelular(celular);

        if (foto != null && !foto.isEmpty()) {
            try {
                String nombreArchivo = UUID.randomUUID() + "_" + foto.getOriginalFilename();
                String rutaFotos = "/home/user1/Documents/fotos/";
                File destino = new File(rutaFotos + nombreArchivo);
                foto.transferTo(destino);
                usuario.setFoto("/fotos-usuarios/" + nombreArchivo);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        usuarioService.update(usuario);
        
        // If this is the current user and email was changed, we need to handle the session
        if (isCurrentUser && !email.equals(principal.getName())) {
            // Return a special response to indicate email change
            return ResponseEntity.ok("emailChanged=true");
        }
        
        return ResponseEntity.ok("Usuario actualizado correctamente");
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

    @GetMapping("/credito/cuotas/{id}")
    public String creditoCuotas(@PathVariable Long id, Model model, Principal principal) {
        // Get current user's name
        String currentUserName = getCurrentUserName(principal);
        model.addAttribute("currentUserName", currentUserName);
        
        String emailUsuario = principal.getName();

        UsuarioEntity usuario = usuarioService.findByEmail(emailUsuario)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        CreditoEntity credito = creditoService.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        
        // Verify that the credit belongs to the current user
        if (!credito.getUsuario().getId().equals(usuario.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No tienes permiso para ver este crédito");
        }
        
        List<CreditoCuotaEntity> cuotas = creditoCuotaService.findByCreditoId(id);
        List<CreditoCuotaEntity> cuotasPendientes = creditoCuotaService.findPendientesByCreditoId(id);
        List<CreditoCuotaEntity> cuotasEnRevision = creditoCuotaService.findEnRevisionByCreditoId(id);
        List<CreditoCuotaEntity> cuotasPagadas = creditoCuotaService.findPagadasByCreditoId(id);
        List<CreditoCuotaEntity> cuotasVencidas = creditoCuotaService.findVencidasByCreditoId(id);
        List<CreditoCuotaEntity> cuotasAvencer = creditoCuotaService.findAVencerByCreditoId(id);

        
        // Add all cuota lists to the model
        model.addAttribute("cuotas", cuotas);
        model.addAttribute("cuotasPendientes", cuotasPendientes);
        model.addAttribute("cuotasEnRevision", cuotasEnRevision);
        model.addAttribute("cuotasPagadas", cuotasPagadas);
        model.addAttribute("cuotasVencidas", cuotasVencidas);
        model.addAttribute("cuotasAvencer", cuotasAvencer);

        return "appDashboard/user/creditoCuotas";
    }
}
