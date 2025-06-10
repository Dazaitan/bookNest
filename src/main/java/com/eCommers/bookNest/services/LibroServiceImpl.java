package com.eCommers.bookNest.services;

import com.eCommers.bookNest.entity.Libro;
import com.eCommers.bookNest.repository.LibroRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LibroServiceImpl implements LibroService{
    private final LibroRepository libroRepository;
    private final NotificacionServicesImpl notificacionServicesImpl;

    public LibroServiceImpl(LibroRepository libroRepository, NotificacionServicesImpl notificacionServicesImpl) {
        this.libroRepository = libroRepository;
        this.notificacionServicesImpl = notificacionServicesImpl;
    }

    @Override
    public Libro crearLibro(Libro libro) {
        return libroRepository.save(libro);
    }

    @Override
    public Optional<Libro> obtenerLibroPorId(Long id) {
        System.out.println("Ejecutando obtener libro por ID");
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
                    boolean cambioPrecio = !libro.getPrecio().equals(libroDetalles.getPrecio());
                    boolean cambioStock = !libro.getStock().equals(libroDetalles.getStock());

                    libro.setTitulo(libroDetalles.getTitulo());
                    libro.setAutor(libroDetalles.getAutor());
                    libro.setPrecio(libroDetalles.getPrecio());
                    libro.setStock(libroDetalles.getStock());

                    // Notificar cambios a los usuarios
                    if (cambioPrecio) {
                        notificacionServicesImpl.crearNotificacionGlobal("El libro '" + libro.getTitulo() + "' ha cambiado de precio a $" + libro.getPrecio());
                    }
                    if (cambioStock) {
                        notificacionServicesImpl.crearNotificacionGlobal("El stock del libro '" + libro.getTitulo() + "' ha cambiado a " + libro.getStock() + " unidades.");
                    }
                    return libroRepository.save(libro);
                })
                .orElseThrow(() -> new RuntimeException("Libro no encontrado"));
    }

    @Override
    public void eliminarLibro(Long id) {
        libroRepository.deleteById(id);
    }
}
