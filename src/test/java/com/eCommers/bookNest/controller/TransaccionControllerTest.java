package com.eCommers.bookNest.controller;

import com.eCommers.bookNest.config.SecurityConfig;
import com.eCommers.bookNest.config.jwt.JwtService;
import com.eCommers.bookNest.entity.Orden;
import com.eCommers.bookNest.entity.Transaccion;
import com.eCommers.bookNest.entity.Usuario;
import com.eCommers.bookNest.services.TransaccionServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TransaccionController.class)
@Import({SecurityConfig.class, JwtService.class})
@AutoConfigureMockMvc
class TransaccionControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TransaccionServiceImpl transaccionService;

    @MockitoBean
    private JwtService jwtService;

    @Autowired
    private ObjectMapper objectMapper;

    private String userToken;
    private Usuario usuarioPrueba;
    private Orden ordenPrueba;

    @BeforeEach
    void setUp() {
        // Configurar usuario de prueba
        usuarioPrueba = new Usuario();
        usuarioPrueba.setId(1L);
        usuarioPrueba.setCorreo("usuario@test.com");

        // Configurar orden de prueba
        ordenPrueba = new Orden();
        ordenPrueba.setId(1L);

        // Configurar token válido
        Claims userClaims = mock(Claims.class);
        when(userClaims.getSubject()).thenReturn("usuario@test.com");
        when(userClaims.get("rol")).thenReturn("USER");
        when(jwtService.validarToken("token-valido")).thenReturn(userClaims);

        userToken = "Bearer token-valido";
    }

    @Test
    void obtenerHistorialUsuario_Autenticado_DeberiaRetornarHistorial() throws Exception {
        // Configurar datos de prueba
        Transaccion transaccion1 = new Transaccion();
        transaccion1.setId(1L);
        transaccion1.setUsuario(usuarioPrueba);
        transaccion1.setOrden(ordenPrueba);
        transaccion1.setFecha(LocalDateTime.of(2023, 5, 15, 10, 30));
        transaccion1.setMontoTotal(150.75);
        transaccion1.setMetodoPago("TARJETA_CREDITO");

        Transaccion transaccion2 = new Transaccion();
        transaccion2.setId(2L);
        transaccion2.setUsuario(usuarioPrueba);
        transaccion2.setOrden(ordenPrueba);
        transaccion2.setFecha(LocalDateTime.of(2023, 5, 10, 14, 15));
        transaccion2.setMontoTotal(99.99);
        transaccion2.setMetodoPago("PAYPAL");

        List<Transaccion> transacciones = Arrays.asList(transaccion1, transaccion2);

        when(transaccionService.obtenerHistorialPorCorreo("usuario@test.com"))
                .thenReturn(transacciones);

        // Ejecutar y verificar
        mockMvc.perform(get("/transacciones/usuario")
                        .header("Authorization", userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].montoTotal").value(150.75))
                .andExpect(jsonPath("$[0].metodoPago").value("TARJETA_CREDITO"))
                .andExpect(jsonPath("$[0].fecha").value("2023-05-15T10:30:00"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].montoTotal").value(99.99))
                .andExpect(jsonPath("$[1].metodoPago").value("PAYPAL"))
                .andExpect(jsonPath("$[1].fecha").value("2023-05-10T14:15:00"));
    }
}