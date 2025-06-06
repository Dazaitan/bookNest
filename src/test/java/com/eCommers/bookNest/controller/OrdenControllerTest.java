package com.eCommers.bookNest.controller;

import com.eCommers.bookNest.config.jwt.JwtService;
import com.eCommers.bookNest.entity.EstadoOrden;
import com.eCommers.bookNest.entity.Orden;
import com.eCommers.bookNest.entity.Usuario;
import com.eCommers.bookNest.services.OrdenServiceImpl;
import com.eCommers.bookNest.services.UsuarioService;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.ArrayList;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
class OrdenControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private OrdenServiceImpl ordenServiceImpl;

    @MockitoBean
    private UsuarioService usuarioService;

    @Container
    private static final PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:latest")
                    .withDatabaseName("test_db")
                    .withUsername("test")
                    .withPassword("test");

    private String tokenMock;
    @BeforeEach
    void setUp() throws Exception {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();

        System.out.println("🔍 Base de datos de Testcontainers iniciada en: " + postgres.getJdbcUrl());

        // Generar un JWT válido con `JwtService`
        Mockito.when(jwtService.generarToken(Mockito.anyString(), Mockito.anyString()))
                .thenReturn("mi-token-mockeado");
        tokenMock = "Bearer " + jwtService.generarToken("testUser", "CLIENTE");
        System.out.println("🔍 Token generado en setUp: " + tokenMock);

        // Simular `Claims`
        Claims claimsMock = Mockito.mock(Claims.class);
        Mockito.when(claimsMock.getSubject()).thenReturn("testUser");
        Mockito.when(claimsMock.get("rol")).thenReturn("CLIENTE");

        Mockito.when(jwtService.validarToken(Mockito.anyString())).thenReturn(claimsMock);

        // Simular usuario obtenido por correo
        Usuario usuarioMock = new Usuario();
        usuarioMock.setId(1L);
        usuarioMock.setCorreo("test@correo.com");

        Mockito.when(usuarioService.obtenerUsuarioPorCorreo(Mockito.anyString()))
                .thenReturn(usuarioMock);

        // Simular orden creada
        Orden ordenMock = new Orden();
        ordenMock.setId(1L);
        ordenMock.setUsuario(usuarioMock);
        ordenMock.setEstado(EstadoOrden.PENDIENTE);
        ordenMock.setLibrosOrdenados(new ArrayList<>());

        Mockito.when(ordenServiceImpl.crearOrden(Mockito.any(Orden.class)))
                .thenReturn(ordenMock);
    }

    @Test
    void crearOrden_DeberiaRetornar200() throws Exception {
        System.out.println("🔍 Ejecutando prueba de crearOrden...");
        System.out.println("🔍 Token enviado: " + tokenMock);

        MvcResult result = mockMvc.perform(post("/ordenes/crear")
                        .header("Authorization", tokenMock) //Enviar token en la peticion
                        .contentType("application/json")
                        .content("{\"usuarioId\": 1, \"estado\": \"PENDIENTE\"}"))
                .andExpect(status().isOk())
                .andReturn();

        System.out.println("🔍 Respuesta JSON: " + result.getResponse().getContentAsString());

        mockMvc.perform(post("/ordenes/crear")
                        .header("Authorization", tokenMock)
                        .contentType("application/json")
                        .content("{\"usuarioId\": 1, \"estado\": \"PENDIENTE\"}"))
                .andExpect(jsonPath("$.estado").value("PENDIENTE"));
    }
}
