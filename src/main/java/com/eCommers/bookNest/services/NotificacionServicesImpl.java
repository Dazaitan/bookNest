package com.eCommers.bookNest.services;

import com.eCommers.bookNest.entity.Notificacion;
import com.eCommers.bookNest.entity.Usuario;
import com.eCommers.bookNest.repository.NotificacionRepository;
import com.eCommers.bookNest.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
@Service
public class NotificacionServicesImpl implements NotificacionService{
    private final NotificacionRepository notificacionRepository;
    private final UsuarioRepository usuarioRepository;

    public NotificacionServicesImpl(NotificacionRepository notificacionRepository, UsuarioRepository usuarioRepository) {
        this.notificacionRepository = notificacionRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public void crearNotificacion(Long usuarioId, String mensaje) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Notificacion notificacion = new Notificacion();
        notificacion.setUsuario(usuario);
        notificacion.setMensaje(mensaje);
        notificacion.setFecha(LocalDateTime.now());
        notificacion.setLeido(false);

        notificacionRepository.save(notificacion);
    }

    @Override
    public void marcarComoLeido(Long id) {
        Notificacion notificacion = notificacionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notificación no encontrada"));
        notificacion.setLeido(true);
        notificacionRepository.save(notificacion);
    }

    @Override
    public void crearNotificacionGlobal(String mensaje) {
        List<Usuario> usuarios = usuarioRepository.findAll();

        for (Usuario usuario : usuarios) {
            Notificacion notificacion = new Notificacion();
            notificacion.setUsuario(usuario);
            notificacion.setMensaje(mensaje);
            notificacion.setFecha(LocalDateTime.now());
            notificacion.setLeido(false);

            notificacionRepository.save(notificacion);
        }
    }

    @Override
    public List<Notificacion> obtenerNotificacionesPorCorreo(String correo) {
        Usuario usuario = usuarioRepository.findByCorreo(correo)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        return notificacionRepository.findByUsuarioId(usuario.getId());
    }
}
