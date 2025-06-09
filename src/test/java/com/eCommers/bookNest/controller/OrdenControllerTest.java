package com.eCommers.bookNest.controller;

import com.eCommers.bookNest.config.jwt.JwtService;
import com.eCommers.bookNest.dto.OrdenDTO;
import com.eCommers.bookNest.dto.OrdenLibroDTO;
import com.eCommers.bookNest.entity.*;
import com.eCommers.bookNest.services.OrdenServiceImpl;
import com.eCommers.bookNest.services.UsuarioService;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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
    private Usuario usuarioMock;
    private Orden ordenMock;

    @BeforeEach
    void setUp() throws Exception {
        //Mocks para simulaciones
        usuarioMock = new Usuario();
        usuarioMock.setId(1L);
        usuarioMock.setCorreo("cliente@test.com");
        usuarioMock.setRol(Rol.CLIENTE);

        ordenMock = new Orden();
        ordenMock.setId(1L);
        ordenMock.setUsuario(usuarioMock);
        ordenMock.setEstado(EstadoOrden.PENDIENTE);

        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();

        System.out.println("🔍 Base de datos de Testcontainers iniciada en: " + postgres.getJdbcUrl());

        // Generar un JWT válido con `JwtService` con rol CLIENTE
        Mockito.when(jwtService.generarToken(Mockito.anyString(), Mockito.anyString()))
                .thenReturn("mi-token-mockeado");
        tokenMock = "Bearer " + jwtService.generarToken("testUser", "CLIENTE");

        // Simular `Claims`
        Claims claimsMock = Mockito.mock(Claims.class);
        Mockito.when(claimsMock.getSubject()).thenReturn("testUser");
        Mockito.when(claimsMock.get("rol")).thenReturn("CLIENTE");

        Mockito.when(jwtService.validarToken(Mockito.anyString())).thenReturn(claimsMock);

    }

    @Test
    void crearOrden_DeberiaRetornar200() throws Exception {
        System.out.println("🔍 Ejecutando prueba de crearOrden...");

        Mockito.when(usuarioService.obtenerUsuarioPorCorreo(Mockito.anyString()))
                .thenReturn(usuarioMock);

        ordenMock.setLibrosOrdenados(new ArrayList<>());

        Mockito.when(ordenServiceImpl.crearOrden(Mockito.any(Orden.class)))
                .thenReturn(ordenMock);

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

    @Test
    void obtenerOrdenExistente() throws Exception {
        System.out.println("🔍 Ejecutando prueba de obtenerOrdenExistente...");

        List<OrdenLibro> librosMock = List.of(
                new OrdenLibro(1L, ordenMock, new Libro(1L, "Libro 1", "Autor 1", 30.0, 10), 2, 30.0),
                new OrdenLibro(2L, ordenMock, new Libro(2L, "Libro 2", "Autor 2", 25.0, 5), 1, 25.0)
        );

        ordenMock.setLibrosOrdenados(librosMock);

        Mockito.when(ordenServiceImpl.obtenerOrdenPorId(1L)).thenReturn(Optional.of(ordenMock));

        mockMvc.perform(get("/ordenes/1")
                        .header("Authorization", tokenMock))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.usuarioId").value(1))
                .andExpect(jsonPath("$.estado").value("PENDIENTE"))
                .andExpect(jsonPath("$.librosOrdenados[0].titulo").value("Libro 1"))
                .andExpect(jsonPath("$.librosOrdenados[1].titulo").value("Libro 2"));
    }

    @Test
    @WithMockUser(username = "cliente@test.com", roles = {"CLIENTE"}) // Simula un usuario con rol CLIENTE
    void obtenerOrdenesUsuarioExistente() throws Exception {
        System.out.println("🔍 Ejecutando prueba de obtenerOrdenesUsuarioExistente...");

        List<OrdenLibro> librosMock = List.of(
                new OrdenLibro(null, new Orden(), new Libro(1L, "Libro 1", "Autor 1", 30.0, 10), 2, 30.0),
                new OrdenLibro(null, new Orden(), new Libro(2L, "Libro 2", "Autor 2", 25.0, 5), 1, 25.0)
        );

        ordenMock.setLibrosOrdenados(librosMock);

        Mockito.when(ordenServiceImpl.obtenerOrdenesPorUsuario(1L)).thenReturn(List.of(ordenMock));

        mockMvc.perform(get("/ordenes/usuario/1")
                        .header("Authorization", tokenMock))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].usuarioId").value(1))
                .andExpect(jsonPath("$[0].estado").value("PENDIENTE"))
                .andExpect(jsonPath("$[0].librosOrdenados[0].titulo").value("Libro 1"))
                .andExpect(jsonPath("$[0].librosOrdenados[1].titulo").value("Libro 2"));
    }

    @Test
    @WithMockUser(username = "admin@test.com", roles = {"ADMIN"}) // Simula un usuario con rol ADMIN
    void actualizarEstadoOrdenExistente() throws Exception {
        System.out.println("🔍 Ejecutando prueba de actualizarEstadoOrdenExistente...");

        // Simular usuario ADMIN
        Usuario usuarioAdmin = new Usuario();
        usuarioAdmin.setId(2L);
        usuarioAdmin.setCorreo("admin@test.com");
        usuarioAdmin.setRol(Rol.ADMIN);

        ordenMock.setUsuario(usuarioAdmin);

        // Generar JWT solo para ADMIN
        Mockito.when(jwtService.generarToken("adminUser", "ADMIN")).thenReturn("mi-token-admin");
        String adminTokenMock = "Bearer " + jwtService.generarToken("adminUser", "ADMIN");

        // Simular `Claims` de ADMIN
        Claims claimsMockAdmin = Mockito.mock(Claims.class);
        Mockito.when(claimsMockAdmin.getSubject()).thenReturn("adminUser");
        Mockito.when(claimsMockAdmin.get("rol")).thenReturn("ADMIN");
        Mockito.when(jwtService.validarToken("mi-token-admin")).thenReturn(claimsMockAdmin);

        mockMvc.perform(put("/ordenes/actualizar-estado/1")
                        .header("Authorization", adminTokenMock)
                        .contentType("application/json")
                        .content("\"ENVIADA\"")) // Enviar el nuevo estado en el cuerpo de la petición
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    @WithMockUser(username = "cliente@test.com", roles = {"CLIENTE"}) // Simula un usuario con rol CLIENTE
    void completarOrdenExistente() throws Exception {
        System.out.println("🔍 Ejecutando prueba de completarOrdenExistente...");

        List<OrdenLibroDTO> librosDTO = List.of(
                new OrdenLibroDTO(1L, "Libro 1", 2, 30.0),
                new OrdenLibroDTO(2L, "Libro 2", 1, 25.0)
        );

        OrdenDTO ordenCompletada = new OrdenDTO();
        ordenCompletada.setId(1L);
        ordenCompletada.setUsuarioId(1L);
        ordenCompletada.setFecha(LocalDateTime.now());
        ordenCompletada.setEstado(EstadoOrden.COMPLETADA);
        ordenCompletada.setLibrosOrdenados(librosDTO);

        Mockito.when(ordenServiceImpl.completarOrden(1L, "TARJETA")).thenReturn(ordenCompletada);

        mockMvc.perform(put("/ordenes/completar/1")
                        .header("Authorization", tokenMock)
                        .contentType("application/json")
                        .content("\"TARJETA\""))
                .andExpect(status().isOk())
                .andReturn();
    }


}
