package com.eCommers.bookNest.services;

import com.eCommers.bookNest.entity.Orden;
import com.eCommers.bookNest.entity.Transaccion;

import java.util.List;

public interface TransaccionService {
    void registrarTransaccion(Long usuarioId, Orden orden, String metodoPago);
    List<Transaccion> obtenerHistorialPorCorreo(String correo);
}