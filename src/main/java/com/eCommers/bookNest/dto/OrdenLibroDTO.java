package com.eCommers.bookNest.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class OrdenLibroDTO {
    private Long libroId;
    private String titulo;
    private Integer cantidad;
    private Double precioUnitario;
}
