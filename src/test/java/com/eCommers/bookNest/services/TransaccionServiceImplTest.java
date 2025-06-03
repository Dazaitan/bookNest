package com.eCommers.bookNest.services;

import com.eCommers.bookNest.entity.*;
import com.eCommers.bookNest.repository.TransaccionRepository;
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
class TransaccionServiceImplTest {

    @Mock
    private TransaccionRepository transaccionRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private TransaccionServiceImpl transaccionServiceImpl;

    private Usuario usuarioMock;
    private Orden ordenMock;
    private Transaccion transaccionMock;

    @BeforeEach
    void setUp() {
        usuarioMock = new Usuario();
        usuarioMock.setId(1L);
        usuarioMock.setCorreo("test@example.com");

        ordenMock = new Orden();
        ordenMock.setId(100L);
        ordenMock.setUsuario(usuarioMock);

        OrdenLibro ordenLibroMock = new OrdenLibro();
        ordenLibroMock.setLibro(new Libro(10L, "Libro de prueba", "Autor Desconocido", 20.0, 10));
        ordenLibroMock.setCantidad(2);
        ordenLibroMock.setPrecioUnitario(20.0);

        ordenMock.setLibrosOrdenados(List.of(ordenLibroMock));

        transaccionMock = new Transaccion();
        transaccionMock.setId(10L);
        transaccionMock.setUsuario(usuarioMock);
        transaccionMock.setOrden(ordenMock);
        transaccionMock.setFecha(LocalDateTime.now());
        transaccionMock.setMontoTotal(50.0);
        transaccionMock.setMetodoPago("Tarjeta de crédito");
    }

    @Test
    void registrarTransaccion_DeberiaGuardarTransaccion() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioMock));
        when(transaccionRepository.save(any(Transaccion.class))).thenReturn(transaccionMock);

        transaccionServiceImpl.registrarTransaccion(1L, ordenMock, "Tarjeta de crédito");

        verify(transaccionRepository, times(1)).save(any(Transaccion.class));
    }

    @Test
    void obtenerHistorialPorCorreo_DeberiaRetornarTransaccionesSiUsuarioExiste() {
        when(usuarioRepository.findByCorreo("test@example.com")).thenReturn(Optional.of(usuarioMock));
        when(transaccionRepository.findByUsuarioId(1L)).thenReturn(List.of(transaccionMock));

        List<Transaccion> historial = transaccionServiceImpl.obtenerHistorialPorCorreo("test@example.com");

        assertFalse(historial.isEmpty());
        assertEquals(1, historial.size());
        assertEquals(transaccionMock.getId(), historial.get(0).getId());
    }
}
