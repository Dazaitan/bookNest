package com.eCommers.bookNest.services;

import com.eCommers.bookNest.entity.*;
import com.eCommers.bookNest.repository.LibroRepository;
import com.eCommers.bookNest.repository.OrdenRepository;
import com.eCommers.bookNest.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class OrdenServiceImpl implements OrdenService{
    private final OrdenRepository ordenRepository;
    private final UsuarioRepository usuarioRepository;
    private final LibroRepository libroRepository;

    public OrdenServiceImpl(OrdenRepository ordenRepository, UsuarioRepository usuarioRepository, LibroRepository libroRepository) {
        this.ordenRepository = ordenRepository;
        this.usuarioRepository = usuarioRepository;
        this.libroRepository = libroRepository;
    }

    @Override
    public Orden crearOrden(Orden orden) {
        Usuario usuario = usuarioRepository.findById(orden.getUsuario().getId())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        orden.setUsuario(usuario);
        orden.setEstado(EstadoOrden.PENDIENTE);

        for (OrdenLibro ordenLibro : orden.getLibrosOrdenados()) {
            Libro libro = libroRepository.findById(ordenLibro.getLibro().getId())
                    .orElseThrow(() -> new RuntimeException("Libro no encontrado"));

            ordenLibro.setOrden(orden);
            ordenLibro.setLibro(libro);
        }
        return ordenRepository.save(orden);
    }

    @Override
    public Optional<Orden> obtenerOrdenPorId(Long id) {
        return ordenRepository.findById(id);
    }

    @Override
    public List<Orden> obtenerOrdenesPorUsuario(Long usuarioId) {
        return ordenRepository.findByUsuarioId(usuarioId);
    }
}
