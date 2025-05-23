package com.eCommers.bookNest.controller;

import com.eCommers.bookNest.dto.TransaccionDTO;
import com.eCommers.bookNest.services.TransaccionService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/transacciones")
public class TransaccionController {
    private final TransaccionService transaccionService;

    public TransaccionController(TransaccionService transaccionService) {
        this.transaccionService = transaccionService;
    }

    @GetMapping("/usuario")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<TransaccionDTO>> obtenerHistorialUsuario(Authentication authentication) {
        String correoUsuario = authentication.getName();
        List<TransaccionDTO> historial = transaccionService.obtenerHistorialPorCorreo(correoUsuario)
                .stream()
                .map(TransaccionDTO::new)
                .collect(Collectors.toList());

        return ResponseEntity.ok(historial);
    }
}
