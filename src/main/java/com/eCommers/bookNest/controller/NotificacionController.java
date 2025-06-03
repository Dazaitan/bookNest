package com.eCommers.bookNest.controller;

import com.eCommers.bookNest.entity.Notificacion;
import com.eCommers.bookNest.services.NotificacionServicesImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notificaciones")
public class NotificacionController {
    private final NotificacionServicesImpl notificacionServiceImpl;

    public NotificacionController(NotificacionServicesImpl notificacionServiceImpl) {
        this.notificacionServiceImpl = notificacionServiceImpl;
    }

    @GetMapping("/usuario")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<Notificacion>> obtenerNotificacionesUsuario(Authentication authentication) {
        String correoUsuario = authentication.getName();
        List<Notificacion> notificaciones = notificacionServiceImpl.obtenerNotificacionesPorCorreo(correoUsuario);
        return ResponseEntity.ok(notificaciones);
    }

    @PutMapping("/marcar-leido/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> marcarComoLeido(@PathVariable Long id) {
        notificacionServiceImpl.marcarComoLeido(id);
        return ResponseEntity.noContent().build();
    }
}
