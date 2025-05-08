package com.eCommers.bookNest.controller;
import com.eCommers.bookNest.services.UsuarioService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final UsuarioService usuarioService;

    public AuthController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestParam String correo, @RequestParam String contrasena) {
        String token = usuarioService.autenticarUsuario(correo, contrasena);
        return ResponseEntity.ok(token);
    }
}
