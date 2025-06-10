package com.eCommers.bookNest.controller;

import com.eCommers.bookNest.config.SecurityConfig;
import com.eCommers.bookNest.config.jwt.JwtService;
import com.eCommers.bookNest.entity.Libro;
import com.eCommers.bookNest.services.LibroServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LibroController.class)
@Import({SecurityConfig.class, JwtService.class}) // Importa tu configuración de seguridad y utilidad JWT
@AutoConfigureMockMvc
class LibroControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private LibroServiceImpl libroService;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private JwtService jwtService;

    private String adminToken;

    @BeforeEach
    void setUp() {
        // Generar tokens antes de cada prueba
        adminToken = "Bearer " + jwtService.generarToken("admin@booknest.com", "ADMIN");
    }

    @Test
    void crearLibro_ConTokenAdminValido_DeberiaRetornarLibroCreado() throws Exception {
        //Simular datos
        Libro libroMock = new Libro();
        libroMock.setTitulo("El Principito");
        libroMock.setAutor("Antoine de Saint-Exupéry");
        //Definir comportamiento cuando se ejecute el metodo
        when(libroService.crearLibro(any(Libro.class))).thenReturn(libroMock);

        //Configurar mocks
        Claims claimsMock = mock(Claims.class);
        when(claimsMock.getSubject()).thenReturn("admin@test.com");
        when(claimsMock.get("rol")).thenReturn("ADMIN");

        //Definir comportamiento cada que se valide el token
        when(jwtService.validarToken(anyString())).thenReturn(claimsMock);

        // Ejecutar petición
        mockMvc.perform(post("/libros/crear")
                        .header("Authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(libroMock)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.titulo").value("El Principito"))
                .andExpect(jsonPath("$.autor").value("Antoine de Saint-Exupéry"));
    }

    @Test
    void crearLibro_ConTokenUsuario_DeberiaRetornarForbidden() throws Exception {
        //Configurar mocks
        Claims userClaims = mock(Claims.class);
        when(userClaims.getSubject()).thenReturn("cliente@booknest.com");
        when(userClaims.get("rol")).thenReturn("CLIENTE");

        //Definir comportamiento cuando se ejecute el metodo
        when(jwtService.validarToken("token-user-valido")).thenReturn(userClaims);

        //Simular datos
        Libro libroRequest = new Libro();
        libroRequest.setTitulo("Libro no autorizado");

        // Ejecutar petición
        mockMvc.perform(post("/libros/crear")
                        .header("Authorization", "Bearer " + "token-user-valido")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(libroRequest)))
                .andExpect(status().isForbidden());
    }

    @Test
    void obtenerLibro_Existente_DeberiaRetornarLibro() throws Exception {
        //simular datos
        Libro libroMock = new Libro();
        libroMock.setId(1L);
        libroMock.setTitulo("Cien años de soledad");
        libroMock.setAutor("Gabriel García Márquez");
        libroMock.setPrecio(19.99);

        //Definir comportamiento cuando se ejecute el metodo
        when(libroService.obtenerLibroPorId(1L)).thenReturn(Optional.of(libroMock));

        // Ejecutar petición
        mockMvc.perform(get("/libros/1")
                        .contentType(MediaType.APPLICATION_JSON))
                // Verificaciones
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.titulo").value("Cien años de soledad"))
                .andExpect(jsonPath("$.autor").value("Gabriel García Márquez"))
                .andExpect(jsonPath("$.precio").value(19.99));
    }

    @Test
    void obtenerLibro_NoExistente_DeberiaRetornarNotFound() throws Exception {
        when(libroService.obtenerLibroPorId(99L)).thenReturn(Optional.empty());
        // Ejecutar petición
        mockMvc.perform(get("/libros/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void actualizarLibro_ConTokenAdminValido_DeberiaActualizar() throws Exception {
        // Configurar mocks
        Claims adminClaims = mock(Claims.class);
        when(adminClaims.getSubject()).thenReturn("admin@booknest.com");
        when(adminClaims.get("rol")).thenReturn("ADMIN");

        // Ejecutar petición
        when(jwtService.validarToken("token-admin-valido")).thenReturn(adminClaims);

        //Simular datos
        Libro libroActualizado = new Libro();
        libroActualizado.setId(1L);
        libroActualizado.setTitulo("Nuevo título");
        libroActualizado.setAutor("Autor actualizado");
        libroActualizado.setPrecio(25.99);
        //Definir comportamiento cuando se ejecute el método
        when(libroService.actualizarLibro(eq(1L), any(Libro.class))).thenReturn(libroActualizado);

        // Ejecutar petición
        mockMvc.perform(put("/libros/actualizar/1")
                        .header("Authorization", "Bearer token-admin-valido")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(libroActualizado)))

                // Verificaciones
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.titulo").value("Nuevo título"))
                .andExpect(jsonPath("$.autor").value("Autor actualizado"))
                .andExpect(jsonPath("$.precio").value(25.99));
    }

    @Test
    void actualizarLibro_NoExistente_DeberiaRetornarNotFound() throws Exception {
        //Configurar mocks
        Claims adminClaims = mock(Claims.class);
        when(adminClaims.getSubject()).thenReturn("admin@booknest.com");
        when(adminClaims.get("rol")).thenReturn("ADMIN");

        //Modificar comportamiento cuando se ejecute el método
        when(jwtService.validarToken("token-admin")).thenReturn(adminClaims);

        //Modificar comportamiento cuando se ejecute el método
        when(libroService.actualizarLibro(eq(99L), any())).thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Libro no encontrado"));
        // Ejecutar petición
        mockMvc.perform(put("/libros/actualizar/99")
                        .header("Authorization", "Bearer token-admin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                //Verificacion
                .andExpect(status().isNotFound());
    }

    @Test
    void eliminarLibro_ConTokenAdminValido_DeberiaRetornarNoContent() throws Exception {
        // Configurar mocks
        Claims adminClaims = mock(Claims.class);
        when(adminClaims.getSubject()).thenReturn("admin@booknest.com");
        when(adminClaims.get("rol")).thenReturn("ADMIN");
        when(jwtService.validarToken("token-admin-valido")).thenReturn(adminClaims);

        doNothing().when(libroService).eliminarLibro(1L);

        // Ejecutar petición
        mockMvc.perform(delete("/libros/eliminar/1")
                        .header("Authorization", "Bearer token-admin-valido"))

                // Verificacion
                .andExpect(status().isNoContent());
    }

    @Test
    void eliminarLibro_ConTokenUsuario_DeberiaRetornarForbidden() throws Exception {
        // Configurar mock para usuario normal
        Claims userClaims = mock(Claims.class);
        when(userClaims.getSubject()).thenReturn("user@booknest.com");
        when(userClaims.get("rol")).thenReturn("CLIENTE");

        //Modificar comportamiento cuando se ejecute el método
        when(jwtService.validarToken("token-user-valido")).thenReturn(userClaims);

        // Ejecutar petición
        mockMvc.perform(delete("/libros/eliminar/1")
                        .header("Authorization", "Bearer token-user-valido"))
                .andExpect(status().isForbidden());

        verify(libroService, never()).eliminarLibro(anyLong());
    }
}