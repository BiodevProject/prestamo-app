package com.biovizion.prestamo911;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.core.Authentication;


import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collection;

@Component
public class CustomAuth implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        String redirectURL = request.getContextPath();

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        boolean matched = false;

        for (GrantedAuthority authority : authorities) {
            System.out.println("ROL AUTENTICADO: " + authority.getAuthority());
            if (authority.getAuthority().equals("ROLE_ADMIN")) {
                redirectURL += "/admin/dashboard";
                matched = true;
                break;
            } else if (authority.getAuthority().equals("ROLE_USER")) {
                redirectURL += "/usuario/dashboard";
                matched = true;
                break;
            }
        }

        // Si no coincidió ningún rol, redirige a una página segura
        if (!matched) {
            redirectURL += "/default-dashboard"; // o cambia por cualquier página segura
        }

        response.sendRedirect(redirectURL);
    }
}
