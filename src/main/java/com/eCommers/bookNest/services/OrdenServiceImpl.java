package com.eCommers.bookNest.services;

import com.eCommers.bookNest.entity.EstadoOrden;
import com.eCommers.bookNest.entity.Orden;
import com.eCommers.bookNest.entity.Usuario;
import com.eCommers.bookNest.repository.OrdenRepository;
import com.eCommers.bookNest.repository.UsuarioRepository;

import java.util.List;
import java.util.Optional;

public class OrdenServiceImpl implements OrdenService{
    private final OrdenRepository ordenRepository;
    private final UsuarioRepository usuarioRepository;

    public OrdenServiceImpl(OrdenRepository ordenRepository, UsuarioRepository usuarioRepository) {
        this.ordenRepository = ordenRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public Orden crearOrden(Orden orden) {
        Usuario usuario = usuarioRepository.findById(orden.getUsuario().getId())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        orden.setUsuario(usuario);
        orden.setEstado(EstadoOrden.PENDIENTE);
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
