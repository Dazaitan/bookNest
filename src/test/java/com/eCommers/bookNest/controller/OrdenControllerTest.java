package com.eCommers.bookNest.controller;

import com.eCommers.bookNest.config.filters.JwtAuthenticationFilter;
import com.eCommers.bookNest.services.OrdenServiceImpl;
import com.eCommers.bookNest.services.UsuarioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class OrdenControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

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

    @BeforeEach
    void setUp() throws Exception {
        System.setProperty("spring.datasource.url", postgres.getJdbcUrl());
        System.setProperty("spring.datasource.username", postgres.getUsername());
        System.setProperty("spring.datasource.password", postgres.getPassword());

        Mockito.doNothing().when(jwtAuthenticationFilter).doFilter(Mockito.any(), Mockito.any(), Mockito.any());
    }

    @Test
    void crearOrden_DeberiaRetornar200() throws Exception {
        mockMvc.perform(post("/ordenes/crear")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"usuarioId\": 1, \"estado\": \"PENDIENTE\"}"))
                .andExpect(status().isOk());
    }
}