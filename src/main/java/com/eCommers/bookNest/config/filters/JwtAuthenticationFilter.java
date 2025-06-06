package com.eCommers.bookNest.config.filters;

import com.eCommers.bookNest.config.jwt.JwtService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    public JwtAuthenticationFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }
    //Cada que se ejecute alguna petición hacia la aplicación
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        //Obtener credenciales del encabezado de autorización
        System.out.println("🔍 Token recibido en JwtAuthenticationFilter: " + request.getHeader("Authorization"));
        System.out.println("🔍 Authentication generada: " + SecurityContextHolder.getContext().getAuthentication());

        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            //Validar el token y extraer claim
            Claims claims = jwtService.validarToken(token);
            String correo = claims.getSubject();
            String rol = claims.containsKey("rol") ? claims.get("rol", String.class) : "CLIENTE";
            System.out.println("🔍 Usuario extraído del token: " + claims.getSubject());
            System.out.println("🔍 Rol extraído del token: " + claims.get("rol"));

            UserDetails userDetails = User.withUsername(claims.getSubject())
                    .password("")
                    .authorities("ROLE_" + rol) // Agrega el rol
                    .build();
            System.out.println("🔍 Claims completos: " + claims);
            System.out.println("🔍 Rol antes de asignarlo en SecurityContextHolder: " + rol);
            Authentication autenticacion = new UsernamePasswordAuthenticationToken(
                    correo, null, Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + rol)));


            SecurityContextHolder.getContext().setAuthentication(autenticacion);
            System.out.println("🔍 Authentication establecida en SecurityContextHolder: " + SecurityContextHolder.getContext().getAuthentication());
        }
        filterChain.doFilter(request, response);
    }
}
