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
        return ResponseEntity.ok(ordenDTO);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<Orden> obtenerOrden(@PathVariable Long id) {
        return ordenServiceImpl.obtenerOrdenPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/usuario/{id}")
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<List<Orden>> obtenerOrdenesUsuario(@PathVariable Long id) {
        List<Orden> ordenes = ordenServiceImpl.obtenerOrdenesPorUsuario(id);
        return ResponseEntity.ok(ordenes);
    }

    @PutMapping("/actualizar-estado/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Orden> actualizarEstadoOrden(@PathVariable Long id, @RequestBody EstadoOrden nuevoEstado) {
        Orden actualizada = ordenServiceImpl.actualizarEstadoOrden(id, nuevoEstado);
        return ResponseEntity.ok(actualizada);
    }

    @PutMapping("/completar/{id}")
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<Orden> completarOrden(@PathVariable Long id, @RequestBody String metodoPago) {
        Orden ordenCompletada = ordenServiceImpl.completarOrden(id, metodoPago);
        return ResponseEntity.ok(ordenCompletada);
    }

}
