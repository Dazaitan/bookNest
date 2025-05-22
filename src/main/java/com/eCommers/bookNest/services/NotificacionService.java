package com.eCommers.bookNest.services;

import com.eCommers.bookNest.entity.Notificacion;

import java.util.List;

public interface NotificacionService {
    void crearNotificacion(Long usuarioId, String mensaje);
    void marcarComoLeido(Long id);
    void crearNotificacionGlobal(String mensaje);
    List<Notificacion>obtenerNotificacionesPorCorreo(String correo);
}
