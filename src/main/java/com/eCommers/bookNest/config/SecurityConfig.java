package com.eCommers.bookNest.config;

import com.eCommers.bookNest.config.filters.JwtAuthenticationFilter;
import com.eCommers.bookNest.services.UsuarioDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())//desactivar la protección contra ataques CSRF (Cross-Site Request Forgery).
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/login").permitAll()//Habilitar para todos el endpoint de autenticación (/auth/login)
                        .requestMatchers("/usuarios/**").permitAll()
                        .requestMatchers("/libros/**").hasRole("ADMIN")
                        .requestMatchers("/ordenes/**").hasRole("CLIENTE")
                        .anyRequest().authenticated())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        /*
        * addFilterBefore Ejecuta el filtro antes de que Spring gestione la seguridad
        * para validar si hay un token valido en la solicitud
        * */

        return http.build();
    }
}
