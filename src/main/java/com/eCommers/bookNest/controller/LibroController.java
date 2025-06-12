package com.eCommers.bookNest.controller;

import com.eCommers.bookNest.entity.Libro;
import com.eCommers.bookNest.services.LibroServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/libros")
public class LibroController {
    private final LibroServiceImpl libroService;

    public LibroController(LibroServiceImpl libroService) {
        this.libroService = libroService;
    }

    @PostMapping("/crear")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Libro> crearLibro(@RequestBody Libro libro) {
        System.out.println("🔍 Ejecutando Crear libro en el controlador");
        Libro nuevoLibro = libroService.crearLibro(libro);
        return ResponseEntity.ok(nuevoLibro);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Libro> obtenerLibro(@PathVariable Long id) {
        Libro libro = libroService.obtenerLibroPorId(id);
        return ResponseEntity.ok(libro);
    }

    @GetMapping("/todos")
    public ResponseEntity<List<Libro>> listarLibros() {
        List<Libro> libros = libroService.listarLibros();
        return ResponseEntity.ok(libros);
    }

    @PutMapping("/actualizar/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Libro> actualizarLibro(@PathVariable Long id, @RequestBody Libro libroDetalles) {

        Libro actualizado = libroService.actualizarLibro(id, libroDetalles);
        return ResponseEntity.ok(actualizado);
    }

    @DeleteMapping("/eliminar/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> eliminarLibro(@PathVariable Long id) {
        libroService.eliminarLibro(id);
        return ResponseEntity.noContent().build();
    }
}
