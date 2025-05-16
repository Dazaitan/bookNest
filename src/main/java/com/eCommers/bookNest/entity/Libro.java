package com.eCommers.bookNest.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "libros")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Libro {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "libro_id")
    private Long id;

    @Column(nullable = false, length = 255)
    private String titulo;

    @Column(nullable = false, length = 150)
    private String autor;

    @Column(nullable = false)
    private Double precio;

    @Column(nullable = false)
    private Integer stock;

    /*
     *  @ManyToOne
     *  @JoinColumn(name = "orden_id") // Clave foránea para asociar el libro con su orden
     *  private Orden orden;
     *
     * Referencia a la columna en la tabla dentro de la base de datos
     * ¿Si coloco una referencia como clave foránea a la tabla orden
     *  no me limitaría a que cada libro necesariamente
     *  debera tener esta referencia a la tabla de orden?
     * La respuesta es si, si coloco la referencia a la tabla de Orden,
     * no voy a poder por ejemplo; libros que aún no he vendido porque siempre
     * deberán estar dentro de alguna orden.
     *
     * Solución
     *Utilizar una tabla relacional para no forzar la relación entre estas dos tablas
     *
     *  */
}

