package com.eCommers.bookNest.services;

import com.eCommers.bookNest.entity.Libro;

import java.util.List;
import java.util.Optional;

public interface LibroService {
    Libro crearLibro(Libro libro);
    Optional<Libro> obtenerLibroPorId(Long id);
    List<Libro> listarLibros();
    Libro actualizarLibro(Long id, Libro libroDetalles);
    void eliminarLibro(Long id);
}

