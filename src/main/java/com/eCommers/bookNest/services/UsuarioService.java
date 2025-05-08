package com.eCommers.bookNest.services;

import com.eCommers.bookNest.entity.Usuario;
import com.eCommers.bookNest.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
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

    public Optional<Usuario> obtenerUsuarioPorCorreo(String correo) {
        return usuarioRepository.findByCorreo(correo);
    }
    public String autenticarUsuario(String correo, String contrasena) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findByCorreo(correo);
        if (usuarioOpt.isPresent() && passwordService.verificarContrasena(contrasena, usuarioOpt.get().getContrasena())) {
            return jwtService.generarToken(usuarioOpt.get().getCorreo(), usuarioOpt.get().getRol().name());
        }
        throw new RuntimeException("Credenciales incorrectas");
    }
}
