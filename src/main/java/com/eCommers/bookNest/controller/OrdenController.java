package com.eCommers.bookNest.controller;

import com.eCommers.bookNest.dto.OrdenDTO;
import com.eCommers.bookNest.dto.OrdenLibroDTO;
import com.eCommers.bookNest.entity.EstadoOrden;
import com.eCommers.bookNest.entity.Orden;
import com.eCommers.bookNest.entity.Usuario;
import com.eCommers.bookNest.services.NotificacionServicesImpl;
import com.eCommers.bookNest.services.OrdenServiceImpl;
import com.eCommers.bookNest.services.UsuarioService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/ordenes")
public class OrdenController {
    private final OrdenServiceImpl ordenServiceImpl;
    private final UsuarioService usuarioService;
    private final NotificacionServicesImpl notificacionServicesImpl;

    public OrdenController(OrdenServiceImpl ordenService, UsuarioService usuarioService, NotificacionServicesImpl notificacionServicesImpl) {
        this.ordenServiceImpl = ordenService;
        this.usuarioService = usuarioService;
        this.notificacionServicesImpl = notificacionServicesImpl;
    }

    @PostMapping("/crear")
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<OrdenDTO> crearOrden(@RequestBody Orden orden, Authentication authentication) {
        System.out.println("🔍 Authentication: " + authentication);
        String correoUsuario = authentication.getName();
        Usuario usuario = usuarioService.obtenerUsuarioPorCorreo(correoUsuario);

        orden.setUsuario(usuario);

        Orden nuevaOrden = ordenServiceImpl.crearOrden(orden);

        List<OrdenLibroDTO> librosDTO = nuevaOrden.getLibrosOrdenados().stream()
                .map(ordenLibro -> new OrdenLibroDTO(
                        ordenLibro.getLibro().getId(),
                        ordenLibro.getLibro().getTitulo(),
                        ordenLibro.getCantidad(),
                        ordenLibro.getPrecioUnitario()
                ))
                .collect(Collectors.toList());

        OrdenDTO ordenDTO = new OrdenDTO();
        ordenDTO.setId(nuevaOrden.getId());
        ordenDTO.setUsuarioId(nuevaOrden.getUsuario().getId());
        ordenDTO.setFecha(nuevaOrden.getFecha());
        ordenDTO.setEstado(nuevaOrden.getEstado());
        ordenDTO.setLibrosOrdenados(librosDTO);
        notificacionServicesImpl.crearNotificacion(orden.getUsuario().getId(), "Tu orden ha sido creada con estado PENDIENTE.");
        System.out.println("Estado de la orden en DTO: " + ordenDTO.getEstado());
        return ResponseEntity.ok(ordenDTO);
    }

    //Hay que crear un DTO para que me retorne solo la información pertinente a la orden sin repetir información
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<OrdenDTO> obtenerOrden(@PathVariable Long id) {
        return ordenServiceImpl.obtenerOrdenPorId(id)
                .map(orden -> {
                    OrdenDTO dto = new OrdenDTO();
                    dto.setId(orden.getId());
                    dto.setUsuarioId(orden.getUsuario().getId());
                    dto.setFecha(orden.getFecha());
                    dto.setEstado(orden.getEstado());

                    // Convertir la lista de OrdenLibro a OrdenLibroDTO
                    List<OrdenLibroDTO> librosDTO = orden.getLibrosOrdenados().stream()
                            .map(libro -> new OrdenLibroDTO(
                                    libro.getLibro().getId(),
                                    libro.getLibro().getTitulo(),
                                    libro.getCantidad(),
                                    libro.getPrecioUnitario()
                            ))
                            .collect(Collectors.toList());

                    dto.setLibrosOrdenados(librosDTO);

                    return ResponseEntity.ok(dto);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/usuario/{id}")
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<List<OrdenDTO>> obtenerOrdenesUsuario(@PathVariable Long id) {
        List<OrdenDTO> ordenesDTO = ordenServiceImpl.obtenerOrdenesPorUsuario(id).stream()
                .map(orden -> {
                    OrdenDTO dto = new OrdenDTO();
                    dto.setId(orden.getId());
                    dto.setUsuarioId(orden.getUsuario().getId());
                    dto.setFecha(orden.getFecha());
                    dto.setEstado(orden.getEstado());

                    List<OrdenLibroDTO> librosDTO = orden.getLibrosOrdenados().stream()
                            .map(libro -> new OrdenLibroDTO(
                                    libro.getLibro().getId(),
                                    libro.getLibro().getTitulo(),
                                    libro.getCantidad(),
                                    libro.getPrecioUnitario()
                            ))
                            .collect(Collectors.toList());

                    dto.setLibrosOrdenados(librosDTO);

                    return dto;
                })
                .collect(Collectors.toList());

        return (ordenesDTO == null || ordenesDTO.isEmpty())
                ? ResponseEntity.notFound().build()
                : ResponseEntity.ok(ordenesDTO);
    }


    /*Hay algo maal con este endpoint que sigue permitiendole a los usuarios con el Rol de cliente actualizar el estado de las ordenes*/
    @PutMapping("/actualizar-estado/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<OrdenDTO> actualizarEstadoOrden(@PathVariable Long id, @RequestBody EstadoOrden nuevoEstado) {
        OrdenDTO actualizada = ordenServiceImpl.actualizarEstadoOrden(id, nuevoEstado);
        return ResponseEntity.ok(actualizada);
    }

    @PutMapping("/completar/{id}")
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<OrdenDTO> completarOrden(@PathVariable Long id, @RequestBody String metodoPago) {
        OrdenDTO ordenCompletada = ordenServiceImpl.completarOrden(id, metodoPago);
        return ResponseEntity.ok(ordenCompletada);
    }

}
