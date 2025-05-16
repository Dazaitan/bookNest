package com.eCommers.bookNest.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Table(name = "ordenes")
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Orden {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "orden_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false) //clave foránea en la relación con Usuario.
    private Usuario usuario;

    @Column(updatable = false)
    @CreationTimestamp  //Crear fecha automaticamente al crear la orden
    private LocalDateTime fecha;

    @Enumerated(EnumType.STRING)
    private EstadoOrden estado;

    @OneToMany(mappedBy = "orden", cascade = CascadeType.ALL) //mappedBy designa que la relación ya está siendo manejada por otro atributo en la clase OrdenLibro, asi que no me va a generar otra clave foránea en la base de datos
    private List<OrdenLibro> librosOrdenados; //Lista que me permitirá ver los libros asociados a una orden
}