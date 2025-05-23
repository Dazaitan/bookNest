package com.eCommers.bookNest.services;

import com.eCommers.bookNest.entity.Orden;
import com.eCommers.bookNest.entity.Transaccion;
import com.eCommers.bookNest.entity.Usuario;
import com.eCommers.bookNest.repository.TransaccionRepository;
import com.eCommers.bookNest.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TransaccionServiceImpl implements TransaccionService{
    private final TransaccionRepository transaccionRepository;
    private final UsuarioRepository usuarioRepository;

    public TransaccionServiceImpl(TransaccionRepository transaccionRepository, UsuarioRepository usuarioRepository) {
        this.transaccionRepository = transaccionRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public void registrarTransaccion(Long usuarioId, Orden orden, String metodoPago) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        double montoTotal = orden.getLibrosOrdenados().stream()
                .mapToDouble(ordenLibro -> ordenLibro.getCantidad() * ordenLibro.getPrecioUnitario())
                .sum();

        Transaccion transaccion = new Transaccion();
        transaccion.setUsuario(usuario);
        transaccion.setOrden(orden);
        transaccion.setFecha(LocalDateTime.now());
        transaccion.setMontoTotal(montoTotal);
        transaccion.setMetodoPago(metodoPago);

        transaccionRepository.save(transaccion);
    }

    @Override
    public List<Transaccion> obtenerHistorialPorCorreo(String correo) {
        Usuario usuario = usuarioRepository.findByCorreo(correo)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        return transaccionRepository.findByUsuarioId(usuario.getId());
    }
}
