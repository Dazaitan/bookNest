package com.eCommers.bookNest.services;

import com.eCommers.bookNest.entity.Libro;
import com.eCommers.bookNest.repository.LibroRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LibroServiceImplTest {

    @Mock
    private LibroRepository libroRepository;

    @Mock
    private NotificacionServicesImpl notificacionServicesImpl;

    @InjectMocks
    private LibroServiceImpl libroServiceImpl;

    private Libro libroMock;

    @BeforeEach
    void setUp() {
        libroMock = new Libro();
        libroMock.setId(1L);
        libroMock.setTitulo("Libro de prueba");
        libroMock.setAutor("Autor desconocido");
        libroMock.setPrecio(20.0);
        libroMock.setStock(10);
    }

    @Test
    void crearLibro_DeberiaGuardarLibro() {
        when(libroRepository.save(any(Libro.class))).thenReturn(libroMock);

        Libro libroGuardado = libroServiceImpl.crearLibro(libroMock);

        assertNotNull(libroGuardado);
        assertEquals(libroMock.getId(), libroGuardado.getId());
        verify(libroRepository, times(1)).save(libroMock);
    }

    @Test
    void obtenerLibroPorId_DeberiaRetornarLibroSiExiste() {
        when(libroRepository.findById(1L)).thenReturn(Optional.of(libroMock));

        Optional<Libro> libroEncontrado = libroServiceImpl.obtenerLibroPorId(1L);

        assertTrue(libroEncontrado.isPresent());
        assertEquals(libroMock.getId(), libroEncontrado.get().getId());
    }

    @Test
    void listarLibros_DeberiaRetornarListaDeLibros() {
        when(libroRepository.findAll()).thenReturn(List.of(libroMock));

        List<Libro> libros = libroServiceImpl.listarLibros();

        assertFalse(libros.isEmpty());
        assertEquals(1, libros.size());
    }

    @Test
    void actualizarLibro_DeberiaActualizarYNotificarCambios() {
        Libro libroDetalles = new Libro();
        libroDetalles.setTitulo("Nuevo Título");
        libroDetalles.setAutor("Nuevo Autor");
        libroDetalles.setPrecio(25.0);
        libroDetalles.setStock(5);

        when(libroRepository.findById(1L)).thenReturn(Optional.of(libroMock));
        when(libroRepository.save(any(Libro.class))).thenReturn(libroDetalles);

        Libro libroActualizado = libroServiceImpl.actualizarLibro(1L, libroDetalles);

        assertNotNull(libroActualizado);
        assertEquals("Nuevo Título", libroActualizado.getTitulo());
        assertEquals(25.0, libroActualizado.getPrecio());
        verify(notificacionServicesImpl, times(2)).crearNotificacionGlobal(anyString());
    }

    @Test
    void eliminarLibro_DeberiaBorrarLibro() {
        doNothing().when(libroRepository).deleteById(1L);

        libroServiceImpl.eliminarLibro(1L);

        verify(libroRepository, times(1)).deleteById(1L);
    }
}
