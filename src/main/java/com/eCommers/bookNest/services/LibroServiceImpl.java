package com.eCommers.bookNest.services;

import com.eCommers.bookNest.entity.Libro;
import com.eCommers.bookNest.exceptions.ResourceNotFoundException;
import com.eCommers.bookNest.repository.LibroRepository;
import org.springframework.stereotype.Service;

import java.util.List;

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
    public Libro obtenerLibroPorId(Long id) {
        return libroRepository.findById(id).
                orElseThrow(() -> new ResourceNotFoundException("Libro con ID " + id + " no encontrado."));
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
                .orElseThrow(() -> new ResourceNotFoundException("Libro no encontrado"));
    }

    @Override
    public void eliminarLibro(Long id) {
        libroRepository.deleteById(id);
    }
}
