package com.eCommers.bookNest.controller;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import com.eCommers.bookNest.config.SecurityConfig;
import com.eCommers.bookNest.config.jwt.JwtService;
import com.eCommers.bookNest.entity.Notificacion;
import com.eCommers.bookNest.entity.Usuario;
import com.eCommers.bookNest.services.NotificacionServicesImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;

@WebMvcTest(NotificacionController.class)
@Import({SecurityConfig.class, JwtService.class})
@AutoConfigureMockMvc
class NotificacionControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private NotificacionServicesImpl notificacionService;

    @MockitoBean
    private JwtService jwtService;

    @Autowired
    private ObjectMapper objectMapper;

    private String userToken;
    private Usuario usuarioPrueba;

    @BeforeEach
    void setUp() {

        usuarioPrueba = new Usuario();
        usuarioPrueba.setId(1L);
        usuarioPrueba.setCorreo("usuario@test.com");

        // Configurar token de usuario válido
        Claims userClaims = mock(Claims.class);
        when(userClaims.getSubject()).thenReturn("usuario@test.com");
        when(userClaims.get("rol")).thenReturn("USER");
        when(jwtService.validarToken("token-valido")).thenReturn(userClaims);

        userToken = "Bearer token-valido";
    }

    @Test
    void obtenerNotificacionesUsuario_Autenticado_DeberiaRetornarNotificaciones() throws Exception {
        System.out.println("Ejecutando test de notificaciones");
        // Configurar notificaciones de prueba
        Notificacion notificacion1 = new Notificacion();
        notificacion1.setId(1L);
        notificacion1.setUsuario(usuarioPrueba);
        notificacion1.setMensaje("Mensaje de prueba 1");
        notificacion1.setFecha(LocalDateTime.now());
        notificacion1.setLeido(false);

        Notificacion notificacion2 = new Notificacion();
        notificacion2.setId(2L);
        notificacion2.setUsuario(usuarioPrueba);
        notificacion2.setMensaje("Mensaje de prueba 2");
        notificacion2.setFecha(LocalDateTime.now().minusDays(1));
        notificacion2.setLeido(true);

        List<Notificacion> notificaciones = Arrays.asList(notificacion1, notificacion2);

        when(notificacionService.obtenerNotificacionesPorCorreo("usuario@test.com"))
                .thenReturn(notificaciones);

        // Ejecutar y verificar
        mockMvc.perform(get("/notificaciones/usuario")
                        .header("Authorization", userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].mensaje").value("Mensaje de prueba 1"))
                .andExpect(jsonPath("$[0].leido").value(false))
                .andExpect(jsonPath("$[1].leido").value(true));
    }

    @Test
    void marcarComoLeido_Autenticado_DeberiaRetornarNoContent() throws Exception {
        // Configurar notificación existente
        Notificacion notificacion = new Notificacion();
        notificacion.setId(1L);
        notificacion.setUsuario(usuarioPrueba);
        notificacion.setLeido(false);

        mockMvc.perform(put("/notificaciones/marcar-leido/1")
                        .header("Authorization", userToken))
                .andExpect(status().isNoContent());

        // Verificar que se marcó como leído
        verify(notificacionService).marcarComoLeido(1L);
    }
}