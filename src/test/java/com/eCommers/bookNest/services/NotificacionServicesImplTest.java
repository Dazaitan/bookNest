package com.eCommers.bookNest.services;

import com.eCommers.bookNest.entity.Notificacion;
import com.eCommers.bookNest.entity.Usuario;
import com.eCommers.bookNest.repository.NotificacionRepository;
import com.eCommers.bookNest.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificacionServicesImplTest {

    @Mock
    private NotificacionRepository notificacionRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private NotificacionServicesImpl notificacionServiceImpl;

    private Usuario usuarioMock;
    private Notificacion notificacionMock;

    @BeforeEach
    void setUp() {
        usuarioMock = new Usuario();
        usuarioMock.setId(1L);
        usuarioMock.setCorreo("test@example.com");

        notificacionMock = new Notificacion();
        notificacionMock.setId(1L);
        notificacionMock.setUsuario(usuarioMock);
        notificacionMock.setMensaje("Mensaje de prueba");
        notificacionMock.setFecha(LocalDateTime.now());
        notificacionMock.setLeido(false);
    }

    @Test
    void crearNotificacion_DeberiaGuardarNotificacion() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioMock));
        when(notificacionRepository.save(any(Notificacion.class))).thenReturn(notificacionMock);

        notificacionServiceImpl.crearNotificacion(1L, "Mensaje de prueba");

        verify(notificacionRepository, times(1)).save(any(Notificacion.class));
    }

    @Test
    void marcarComoLeido_DeberiaActualizarNotificacion() {
        when(notificacionRepository.findById(1L)).thenReturn(Optional.of(notificacionMock));

        notificacionServiceImpl.marcarComoLeido(1L);

        assertTrue(notificacionMock.isLeido());
        verify(notificacionRepository, times(1)).save(notificacionMock);
    }

    @Test
    void crearNotificacionGlobal_DeberiaCrearNotificacionesParaTodosLosUsuarios() {
        when(usuarioRepository.findAll()).thenReturn(List.of(usuarioMock));
        when(notificacionRepository.save(any(Notificacion.class))).thenReturn(notificacionMock);

        notificacionServiceImpl.crearNotificacionGlobal("Mensaje Global");

        verify(notificacionRepository, times(1)).save(any(Notificacion.class));
    }

    @Test
    void obtenerNotificacionesPorCorreo_DeberiaRetornarListaDeNotificaciones() {
        when(usuarioRepository.findByCorreo("test@example.com")).thenReturn(Optional.of(usuarioMock));
        when(notificacionRepository.findByUsuarioId(1L)).thenReturn(List.of(notificacionMock));

        List<Notificacion> notificaciones = notificacionServiceImpl.obtenerNotificacionesPorCorreo("test@example.com");

        assertFalse(notificaciones.isEmpty());
        assertEquals(1, notificaciones.size());
    }
}
