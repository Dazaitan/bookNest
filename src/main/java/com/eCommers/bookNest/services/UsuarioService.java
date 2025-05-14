package com.eCommers.bookNest.services;

import com.eCommers.bookNest.config.jwt.JwtService;
import com.eCommers.bookNest.entity.Usuario;
import com.eCommers.bookNest.repository.UsuarioRepository;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class UsuarioService {
    private final UsuarioRepository usuarioRepository;
    private final PasswordService passwordService;
    private final JwtService jwtService;

    public UsuarioService(UsuarioRepository usuarioRepository, PasswordService passwordService, JwtService jwtService) {
        this.usuarioRepository = usuarioRepository;
        this.passwordService=passwordService;
        this.jwtService = jwtService;
    }

    public Usuario registrarUsuario(Usuario usuario) {
        usuario.setContrasena(passwordService.encriptarContrasena(usuario.getContrasena()));
        usuario.setFechaRegistro(LocalDateTime.now());
        return usuarioRepository.save(usuario);
    }

    public Usuario obtenerUsuarioPorCorreo(String correo) {
        return usuarioRepository.findByCorreo(correo).
                orElseThrow(() -> new UsernameNotFoundException("Usuario con correo " + correo + " no encontrado"));
    }

    public String autenticarUsuario(String correo, String contrasena) {
        Usuario usuario = usuarioRepository.findByCorreo(correo)
                .orElseThrow(() -> new BadCredentialsException("Correo incorrecto o no registrado"));
        if (!passwordService.verificarContrasena(contrasena, usuario.getContrasena())) {
            throw new BadCredentialsException("Credenciales incorrectas");
        }
        return jwtService.generarToken(usuario.getCorreo(), usuario.getRol().name());
    }
}
