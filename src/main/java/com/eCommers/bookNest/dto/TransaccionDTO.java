package com.eCommers.bookNest.dto;

import com.eCommers.bookNest.entity.Orden;
import com.eCommers.bookNest.entity.Transaccion;
import com.eCommers.bookNest.entity.Usuario;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class TransaccionDTO {
    private Long id;
    private Long ordenId;
    private LocalDateTime fecha;
    private Double montoTotal;
    private String metodoPago;

    public TransaccionDTO(Transaccion transaccion) {
        this.id = transaccion.getId();
        this.fecha = transaccion.getFecha();
        this.montoTotal = transaccion.getMontoTotal();
        this.metodoPago = transaccion.getMetodoPago();
        this.ordenId = transaccion.getOrden().getId(); // Solo el ID
    }

}
