package com.eCommers.bookNest.services;

import com.eCommers.bookNest.entity.EstadoOrden;
import com.eCommers.bookNest.entity.Orden;
import com.eCommers.bookNest.entity.Usuario;
import com.eCommers.bookNest.repository.OrdenRepository;
import com.eCommers.bookNest.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OrdenServiceTest {
    @Mock
    private OrdenRepository ordenRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private OrdenServiceImpl ordenService;

    @Test
    public void testCrearOrden() {
        Usuario usuario = new Usuario();
        usuario.setId(1L);

        Orden orden = new Orden();
        orden.setUsuario(usuario);
        orden.setEstado(EstadoOrden.PENDIENTE);

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(ordenRepository.save(any(Orden.class))).thenReturn(orden);

        Orden resultado = ordenService.crearOrden(orden);

        assertNotNull(resultado);
        assertEquals(EstadoOrden.PENDIENTE, resultado.getEstado());
    }

    @Test
    public void testObtenerOrdenPorId() {
        Orden orden = new Orden();
        orden.setId(1L);

        when(ordenRepository.findById(1L)).thenReturn(Optional.of(orden));

        Optional<Orden> resultado = ordenService.obtenerOrdenPorId(1L);

        assertTrue(resultado.isPresent());
        assertEquals(1L, resultado.get().getId());
    }
}