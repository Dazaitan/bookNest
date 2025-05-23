package com.eCommers.bookNest.services;

import com.eCommers.bookNest.dto.OrdenDTO;
import com.eCommers.bookNest.entity.EstadoOrden;
import com.eCommers.bookNest.entity.Orden;

import java.util.List;
import java.util.Optional;

public interface OrdenService {
    Orden crearOrden(Orden orden);
    Optional<Orden> obtenerOrdenPorId(Long id);
    List<Orden> obtenerOrdenesPorUsuario(Long usuarioId);
    OrdenDTO actualizarEstadoOrden(Long id, EstadoOrden nuevoEstado);
    OrdenDTO completarOrden(Long id, String metodoPago);
}
