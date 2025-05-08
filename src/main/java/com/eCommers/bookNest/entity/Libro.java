package com.eCommers.bookNest.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import org.springframework.data.annotation.Id;

@Entity
public class Libro {
    @Id
    @GeneratedValue
    private Long id;
    private String titulo;
    private String autor;
    private Double precio;
    private Integer stock;
}

