package com.eCommers.bookNest.dto;

import com.eCommers.bookNest.entity.EstadoOrden;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class OrdenDTO {
    private Long id;
    private Long usuarioId;
    private LocalDateTime fecha;
    private EstadoOrden estado;
    private List<OrdenLibroDTO> librosOrdenados;
}
