package com.eCommers.bookNest.services;

import com.eCommers.bookNest.dto.OrdenDTO;
import com.eCommers.bookNest.entity.*;
import com.eCommers.bookNest.repository.LibroRepository;
import com.eCommers.bookNest.repository.OrdenRepository;
import com.eCommers.bookNest.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrdenServiceTest {
    @Mock
    private OrdenRepository ordenRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private LibroRepository libroRepository;

    private OrdenServiceImpl ordenService; //Toco inicializarlo manualmente porque no estaba inyectando las dependencias necesarias

    @Mock
    private TransaccionServiceImpl transaccionServiceImpl;

    @Mock
    private NotificacionServicesImpl notificacionService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        ordenService = new OrdenServiceImpl(ordenRepository, usuarioRepository, libroRepository, notificacionService, transaccionServiceImpl);
    }

    @Test
    void crearOrden_UsuarioYLibrosExistentes_OrdenCreada() {
        Usuario usuarioMock = new Usuario();
        usuarioMock.setId(1L);

        Libro libroMock = new Libro();
        libroMock.setId(10L);

        OrdenLibro ordenLibro = new OrdenLibro();
        ordenLibro.setLibro(libroMock);

        Orden ordenMock = new Orden();
        ordenMock.setUsuario(usuarioMock);
        ordenMock.setLibrosOrdenados(List.of(ordenLibro));

        Mockito.when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioMock));
        Mockito.when(libroRepository.findById(10L)).thenReturn(Optional.of(libroMock));
        Mockito.when(ordenRepository.save(Mockito.any(Orden.class))).thenReturn(ordenMock);

        Orden resultado = ordenService.crearOrden(ordenMock);

        assertNotNull(resultado);
        assertEquals(EstadoOrden.PENDIENTE, resultado.getEstado());
        assertEquals(1L, resultado.getUsuario().getId());
    }

    @Test
    void obtenerOrdenPorId_OrdenExistente_RetornaOrden() {
        Orden ordenMock = new Orden();
        ordenMock.setId(1L);
        ordenMock.setEstado(EstadoOrden.PENDIENTE);
        /*¿Que significa 1L?
        * es una forma de representar un número entero de tipo long.
        * */

        // Simular respuesta del repositorio
        Mockito.when(ordenRepository.findById(1L)).thenReturn(Optional.of(ordenMock));

        // Ejecutar método del servicio
        Optional<Orden> resultado = ordenService.obtenerOrdenPorId(1L);

        // Validar resultado
        assertTrue(resultado.isPresent());
        assertEquals(EstadoOrden.PENDIENTE, resultado.get().getEstado());
    }

    @Test
    void actualizarEstadoOrden_OrdenExistente_ActualizaEstado() {
        Orden ordenMock = new Orden();
        ordenMock.setId(1L);
        ordenMock.setEstado(EstadoOrden.PENDIENTE);


        OrdenLibro ordenLibroMock = new OrdenLibro();
        ordenLibroMock.setLibro(new Libro(10L, "Libro de prueba", "Autor Desconocido", 20.0, 10));
        ordenLibroMock.setCantidad(2);
        ordenLibroMock.setPrecioUnitario(20.0);

        ordenMock.setLibrosOrdenados(List.of(ordenLibroMock));

        Usuario usuarioMock = new Usuario();
        usuarioMock.setId(2L);
        ordenMock.setUsuario(usuarioMock);

        Mockito.when(ordenRepository.findById(1L)).thenReturn(Optional.of(ordenMock));
        Mockito.when(ordenRepository.save(Mockito.any(Orden.class))).
                thenAnswer(invocation ->{
                    Orden ordenGuardada = invocation.getArgument(0);
                    ordenGuardada.setEstado(EstadoOrden.COMPLETADA);
                    return ordenGuardada;
                });

        Mockito.doNothing().when(notificacionService).crearNotificacion(Mockito.anyLong(), Mockito.anyString());

        OrdenDTO resultado = ordenService.actualizarEstadoOrden(ordenMock.getId(), EstadoOrden.COMPLETADA);

        assertNotNull(resultado);
        assertEquals(EstadoOrden.COMPLETADA, resultado.getEstado());
    }

    @Test
    void completarOrden_OrdenExistente_ActualizaEstadoYRegistraTransaccion() {
        Orden ordenMock = new Orden();
        ordenMock.setId(1L);
        ordenMock.setEstado(EstadoOrden.PENDIENTE);

        OrdenLibro ordenLibroMock = new OrdenLibro();
        ordenLibroMock.setLibro(new Libro(10L, "Libro de prueba", "Autor Desconocido", 20.0, 10));
        ordenLibroMock.setCantidad(2);
        ordenLibroMock.setPrecioUnitario(20.0);

        ordenMock.setLibrosOrdenados(List.of(ordenLibroMock));

        Usuario usuarioMock = new Usuario();
        usuarioMock.setId(2L);
        ordenMock.setUsuario(usuarioMock);

        Mockito.when(ordenRepository.findById(1L)).thenReturn(Optional.of(ordenMock));
        Mockito.when(ordenRepository.save(Mockito.any(Orden.class))).thenReturn(ordenMock);
        Mockito.doNothing().when(transaccionServiceImpl).registrarTransaccion(Mockito.anyLong(), Mockito.any(Orden.class), Mockito.anyString());

        OrdenDTO resultado = ordenService.completarOrden(1L, "Tarjeta de crédito");

        assertNotNull(resultado);
        assertEquals(EstadoOrden.COMPLETADA, resultado.getEstado());
    }

}