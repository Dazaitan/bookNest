package com.eCommers.bookNest.services;

import com.eCommers.bookNest.dto.OrdenDTO;
import com.eCommers.bookNest.dto.OrdenLibroDTO;
import com.eCommers.bookNest.entity.*;
import com.eCommers.bookNest.repository.LibroRepository;
import com.eCommers.bookNest.repository.OrdenRepository;
import com.eCommers.bookNest.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class OrdenServiceImpl implements OrdenService{
    private final OrdenRepository ordenRepository;
    private final UsuarioRepository usuarioRepository;
    private final LibroRepository libroRepository;
    private final NotificacionServicesImpl notificacionService;
    private final TransaccionServiceImpl transaccionServiceImpl;

    public OrdenServiceImpl(OrdenRepository ordenRepository, UsuarioRepository usuarioRepository, LibroRepository libroRepository, NotificacionServicesImpl notificacionService, TransaccionServiceImpl transaccionServiceImpl) {
        this.ordenRepository = ordenRepository;
        this.usuarioRepository = usuarioRepository;
        this.libroRepository = libroRepository;
        this.notificacionService = notificacionService;
        this.transaccionServiceImpl = transaccionServiceImpl;
    }

    @Override
    public Orden crearOrden(Orden orden) {
        System.out.println("Ingresando al servicio de Creacion de orden");
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

    @Override
    public OrdenDTO actualizarEstadoOrden(Long id, EstadoOrden nuevoEstado) {
        return ordenRepository.findById(id)
                .map(orden -> {
                    orden.setEstado(nuevoEstado);
                    ordenRepository.save(orden);
                    notificacionService.crearNotificacion(orden.getUsuario().getId(),
                            "Tu orden ha sido actualizada a estado " + nuevoEstado);
                    OrdenDTO dto = new OrdenDTO();
                    dto.setId(orden.getId());
                    dto.setEstado(orden.getEstado());
                    dto.setFecha(orden.getFecha());
                    dto.setUsuarioId(orden.getUsuario().getId());

                    List<OrdenLibroDTO> libroDTO = orden.getLibrosOrdenados().stream()
                            .map(libro ->new OrdenLibroDTO(
                                    libro.getLibro().getId(),
                                    libro.getLibro().getTitulo(),
                                    libro.getCantidad(),
                                    libro.getPrecioUnitario()
                            )).collect(Collectors.toList());;
                    dto.setLibrosOrdenados(libroDTO);
                    return dto;
                })
                .orElseThrow(() -> new RuntimeException("Orden no encontrada"));
    }

    @Override
    public OrdenDTO completarOrden(Long id, String metodoPago) {
        return ordenRepository.findById(id)
                .map(orden -> {
                    orden.setEstado(EstadoOrden.COMPLETADA);
                    ordenRepository.save(orden);

                    // Registrar la transacción
                    transaccionServiceImpl.registrarTransaccion(orden.getUsuario().getId(), orden, metodoPago);
                    OrdenDTO dto = new OrdenDTO();
                    dto.setId(orden.getId());
                    dto.setEstado(orden.getEstado());
                    dto.setFecha(orden.getFecha());
                    dto.setUsuarioId(orden.getUsuario().getId());

                    List<OrdenLibroDTO> libroDTO = orden.getLibrosOrdenados().stream()
                            .map(libro ->new OrdenLibroDTO(
                                    libro.getLibro().getId(),
                                    libro.getLibro().getTitulo(),
                                    libro.getCantidad(),
                                    libro.getPrecioUnitario()
                            )).collect(Collectors.toList());;
                    dto.setLibrosOrdenados(libroDTO);
                    return dto;
                })
                .orElseThrow(() -> new RuntimeException("Orden no encontrada"));
    }
}
