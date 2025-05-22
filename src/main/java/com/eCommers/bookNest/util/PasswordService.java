package com.eCommers.bookNest.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class PasswordService {
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public String encriptarContrasena(String contrasena) {
        return passwordEncoder.encode(contrasena);
    }

    public boolean verificarContrasena(String contrasenaIngresada, String contrasenaHash) {
        return passwordEncoder.matches(contrasenaIngresada, contrasenaHash);
    }
}
