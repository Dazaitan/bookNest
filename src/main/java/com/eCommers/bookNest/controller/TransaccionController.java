package com.eCommers.bookNest.controller;

import com.eCommers.bookNest.entity.Transaccion;
import com.eCommers.bookNest.services.TransaccionService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/transacciones")
public class TransaccionController {
    private final TransaccionService transaccionService;

    public TransaccionController(TransaccionService transaccionService) {
        this.transaccionService = transaccionService;
    }

    @GetMapping("/usuario")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<Transaccion>> obtenerHistorialUsuario(Authentication authentication) {
        String correoUsuario = authentication.getName();
        List<Transaccion> historial = transaccionService.obtenerHistorialPorCorreo(correoUsuario);
        return ResponseEntity.ok(historial);
    }
}
