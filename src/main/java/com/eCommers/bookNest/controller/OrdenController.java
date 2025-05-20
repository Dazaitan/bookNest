package com.eCommers.bookNest.controller;

import com.eCommers.bookNest.dto.OrdenDTO;
import com.eCommers.bookNest.dto.OrdenLibroDTO;
import com.eCommers.bookNest.entity.Orden;
import com.eCommers.bookNest.entity.OrdenLibro;
import com.eCommers.bookNest.services.OrdenServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/ordenes")
public class OrdenController {
    private final OrdenServiceImpl ordenService;

    public OrdenController(OrdenServiceImpl ordenService) {
        this.ordenService = ordenService;
    }

    @PostMapping("/crear")
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<OrdenDTO> crearOrden(@RequestBody Orden orden) {
        Orden nuevaOrden = ordenService.crearOrden(orden);

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

        return ResponseEntity.ok(ordenDTO);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<Orden> obtenerOrden(@PathVariable Long id) {
        return ordenService.obtenerOrdenPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/usuario/{id}")
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<List<Orden>> obtenerOrdenesUsuario(@PathVariable Long id) {
        List<Orden> ordenes = ordenService.obtenerOrdenesPorUsuario(id);
        return ResponseEntity.ok(ordenes);
    }
    /* Test
    @GetMapping("/1")
    public String libroGet(){
        return "Hola mundo desde el controlador de ordenes";
    }*/
}
