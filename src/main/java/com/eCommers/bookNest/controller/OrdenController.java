package com.eCommers.bookNest.controller;

import com.eCommers.bookNest.entity.Orden;
import com.eCommers.bookNest.services.OrdenServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/ordenes")
public class OrdenController {
    private final OrdenServiceImpl ordenService;

    public OrdenController(OrdenServiceImpl ordenService) {
        this.ordenService = ordenService;
    }

    @PostMapping("/crear")
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<Orden> crearOrden(@RequestBody Orden orden) {
        Orden nuevaOrden = ordenService.crearOrden(orden);
        return ResponseEntity.ok(nuevaOrden);
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
