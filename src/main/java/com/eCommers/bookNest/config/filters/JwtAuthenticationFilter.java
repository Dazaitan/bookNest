package com.eCommers.bookNest.config.filters;

import com.eCommers.bookNest.config.jwt.JwtService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

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
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            //Validar el token y extraer claim
            Claims claims = jwtService.validarToken(token);
            String correo = claims.getSubject();
            String rol = claims.get("rol", String.class);

            UserDetails userDetails = User.withUsername(claims.getSubject())
                    .password("")
                    .authorities("ROLE_" + rol) // Agrega el rol
                    .build();
            Authentication autenticacion = new UsernamePasswordAuthenticationToken(correo,null,userDetails.getAuthorities());

            SecurityContextHolder.getContext().setAuthentication(autenticacion);
        }
        filterChain.doFilter(request, response);
    }
}
