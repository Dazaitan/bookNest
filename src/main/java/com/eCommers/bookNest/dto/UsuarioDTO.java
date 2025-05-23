package com.eCommers.bookNest.dto;

import com.eCommers.bookNest.entity.Rol;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UsuarioDTO {
    private Long id;
    private String nombre;
    private String correo;
    private Rol rol;
    private LocalDateTime fechaRegistro;
}
