package com.eCommers.bookNest.services;

import com.eCommers.bookNest.entity.Libro;
import com.eCommers.bookNest.repository.LibroRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LibroServiceImpl implements LibroService{
    private final LibroRepository libroRepository;

    public LibroServiceImpl(LibroRepository libroRepository) {
        this.libroRepository = libroRepository;
    }

    @Override
    public Libro crearLibro(Libro libro) {
        return libroRepository.save(libro);
    }

    @Override
    public Optional<Libro> obtenerLibroPorId(Long id) {
        return libroRepository.findById(id);
    }

    @Override
    public List<Libro> listarLibros() {
        return libroRepository.findAll();
    }

    @Override
    public Libro actualizarLibro(Long id, Libro libroDetalles) {
        return libroRepository.findById(id)
                .map(libro -> {
                    libro.setTitulo(libroDetalles.getTitulo());
                    libro.setAutor(libroDetalles.getAutor());
                    libro.setPrecio(libroDetalles.getPrecio());
                    libro.setStock(libroDetalles.getStock());
                    return libroRepository.save(libro);
                })
                .orElseThrow(() -> new RuntimeException("Libro no encontrado"));
    }

    @Override
    public void eliminarLibro(Long id) {
        libroRepository.deleteById(id);
    }
}
