package com.eCommers.bookNest.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.List;


@Entity
public class Orden {
    @Id
    @GeneratedValue
    private Long id;
    @ManyToOne
    private Usuario usuario;
    @OneToMany
    private List<Libro> libros;
    private LocalDateTime fecha;
    private String estado;
}

