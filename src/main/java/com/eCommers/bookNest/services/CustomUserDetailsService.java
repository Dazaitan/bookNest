package com.eCommers.bookNest.services;

import com.eCommers.bookNest.entity.Usuario;
import com.eCommers.bookNest.repository.UsuarioRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Collection;
import java.util.Collections;

public class CustomUserDetailsService implements UserDetailsService {
    private final UsuarioRepository usuarioRepository;

    public CustomUserDetailsService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }
    @Override
    public UserDetails loadUserByUsername(String correo) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByCorreo(correo)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con correo: " + correo));

        // Asignar roles con el prefijo `ROLE_`
        System.out.println("Cargando usuario");
        String rolString = usuario.getRol().name();
        System.out.println("🔍 Rol asignado en UserDetails: " + rolString);
        Collection<GrantedAuthority> authorities = Collections.singletonList(
                new SimpleGrantedAuthority(rolString.startsWith("ROLE_") ? rolString : "ROLE_" + rolString)
        );


        return new User(usuario.getCorreo(), usuario.getContrasena(), authorities);
    }
}
