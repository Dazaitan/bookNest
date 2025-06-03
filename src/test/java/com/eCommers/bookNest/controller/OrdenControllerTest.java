package com.eCommers.bookNest.controller;

import com.eCommers.bookNest.config.filters.JwtAuthenticationFilter;
import com.eCommers.bookNest.config.jwt.JwtService;
import com.eCommers.bookNest.dto.OrdenDTO;
import com.eCommers.bookNest.entity.EstadoOrden;
import com.eCommers.bookNest.entity.Orden;
import com.eCommers.bookNest.entity.Rol;
import com.eCommers.bookNest.entity.Usuario;
import com.eCommers.bookNest.services.NotificacionServicesImpl;
import com.eCommers.bookNest.services.OrdenServiceImpl;
import com.eCommers.bookNest.services.UsuarioService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrdenController.class)
@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class OrdenControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private OrdenServiceImpl ordenServiceImpl;

    @MockitoBean
    private UsuarioService usuarioService;

    @MockitoBean
    private NotificacionServicesImpl notificacionServicesImpl;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    private static final String TOKEN_PRUEBA = Jwts.builder()
            .setSubject("test@example.com")
            .claim("rol", "CLIENTE")
            .setIssuedAt(new Date())
            .setExpiration(new Date(System.currentTimeMillis() + 3600000)) // Expira en 1 hora
            .signWith(Keys.hmacShaKeyFor("clave_secreta_super_segura_para_pruebas".getBytes()), SignatureAlgorithm.HS256)
            .compact();

    private Usuario usuarioMock;
    private Orden ordenMock;
    private OrdenDTO ordenDTO;

    @BeforeEach
    void setUp() {
        usuarioMock = new Usuario();
        usuarioMock.setId(1L);
        usuarioMock.setCorreo("test@example.com");
        usuarioMock.setRol(Rol.CLIENTE); // Asegurar que el rol no sea null

        ordenMock = new Orden();
        ordenMock.setId(100L);
        ordenMock.setUsuario(usuarioMock);
        ordenMock.setEstado(EstadoOrden.PENDIENTE);

        ordenDTO = new OrdenDTO();
        ordenDTO.setId(100L);
        ordenDTO.setUsuarioId(1L);
        ordenDTO.setEstado(EstadoOrden.PENDIENTE);
    }

    private String generarTokenPrueba() {
        Map<String, Object> claims = new HashMap<>();
        claims.put("rol", "CLIENTE");

        return Jwts.builder()
                .setClaims(claims)
                .setSubject("test@example.com")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 3600000))
                .signWith(Keys.hmacShaKeyFor("clave_secreta_super_segura_para_pruebas".getBytes()), SignatureAlgorithm.HS256)
                .compact();
    }

    @Test
    void crearOrden_DeberiaRetornar200() throws Exception {
        Mockito.when(usuarioService.obtenerUsuarioPorCorreo("test@example.com")).thenReturn(usuarioMock);
        Mockito.when(ordenServiceImpl.crearOrden(Mockito.any(Orden.class))).thenReturn(ordenMock);
        Mockito.when(jwtService.generarToken(Mockito.anyString(), Mockito.anyString()))
                .thenReturn(generarTokenPrueba());

        mockMvc.perform(post("/ordenes/crear")
                        .header("Authorization", "Bearer " + TOKEN_PRUEBA)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ordenMock)))
                .andExpect(status().isOk());

    }

    @Test
    void obtenerOrden_DeberiaRetornar200() throws Exception {
        Mockito.when(ordenServiceImpl.obtenerOrdenPorId(100L)).thenReturn(java.util.Optional.of(ordenMock));

        mockMvc.perform(get("/ordenes/100")
                        .with(SecurityMockMvcRequestPostProcessors.user("test@example.com").roles("CLIENTE")))
                .andExpect(status().isOk());
    }

    @Test
    void actualizarEstadoOrden_DeberiaRetornar200() throws Exception {
        OrdenDTO ordenActualizada = new OrdenDTO();
        ordenActualizada.setId(100L);
        ordenActualizada.setEstado(EstadoOrden.COMPLETADA);

        Mockito.when(ordenServiceImpl.actualizarEstadoOrden(Mockito.eq(100L), Mockito.any(EstadoOrden.class)))
                .thenReturn(ordenActualizada);

        mockMvc.perform(put("/ordenes/actualizar-estado/100")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(EstadoOrden.COMPLETADA))
                        .with(SecurityMockMvcRequestPostProcessors.user("admin@example.com").roles("ADMIN")))
                .andExpect(status().isOk());
    }
}