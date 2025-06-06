package com.eCommers.bookNest.config;

import com.eCommers.bookNest.config.filters.JwtAuthenticationFilter;
import com.eCommers.bookNest.repository.UsuarioRepository;
import com.eCommers.bookNest.services.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
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
                        .requestMatchers("/ordenes/**").hasAnyRole("ADMIN", "CLIENTE")
                        .anyRequest().authenticated());
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        /*
        * addFilterBefore Ejecuta el filtro antes de que Spring gestione la seguridad
        * para validar si hay un token válido en la solicitud
        * */

        return http.build();
    }


    /*
    * Cada que un usuario intente logearse
    * 1️⃣ Spring Security detecta este @Bean y lo usa para cargar los detalles de usuario.
    * 2️⃣ Cuando alguien intenta autenticarse, Spring llama automáticamente a CustomUserDetailsService.loadUserByUsername(correo).*/
    @Bean
    public UserDetailsService userDetailsService(UsuarioRepository usuarioRepository) {
        return new CustomUserDetailsService(usuarioRepository);
    }

}
