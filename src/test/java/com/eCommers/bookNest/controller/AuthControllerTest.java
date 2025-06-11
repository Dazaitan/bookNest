package com.eCommers.bookNest.controller;

import com.eCommers.bookNest.config.jwt.JwtService;
import com.eCommers.bookNest.services.UsuarioService;
import com.eCommers.bookNest.config.filters.JwtAuthenticationFilter; // Para excluirlo si es @Component
import com.eCommers.bookNest.util.PasswordService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print; // Para depuración

@WebMvcTest(
        controllers = AuthController.class, // <-- ¡Ahora testamos AuthController!
        excludeAutoConfiguration = {SecurityAutoConfiguration.class},
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = {JwtAuthenticationFilter.class} // Asegúrate de excluir tu filtro JWT
        ),
        properties = "spring.main.allow-bean-definition-overriding=true"
)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest { // <-- ¡Renombra la clase de test!

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UsuarioService usuarioService;

    @MockitoBean
    private PasswordService passwordService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UserDetailsService userDetailsService;

    private String testCorreo;
    private String testContrasena;
    private String testToken;

    @BeforeEach
    void setUp() {
        testCorreo = "test@booknest.com";
        testContrasena = "password123";
        testToken = "mocked.jwt.token";
    }

    @Test
    void login_conCredencialesValidas_debeRetornarTokenYStatus200() throws Exception {
        when(usuarioService.autenticarUsuario(anyString(), anyString())).thenReturn(testToken);

        mockMvc.perform(post("/auth/login")
                        .param("correo", testCorreo)
                        .param("contrasena", testContrasena)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(testToken));
    }

    @Test
    void login_conCredencialesInvalidas_debeRetornarErrorYStatus500() throws Exception {
        when(usuarioService.autenticarUsuario(anyString(), anyString()))
                .thenThrow(new RuntimeException("Credenciales inválidas"));

        mockMvc.perform(post("/auth/login")
                        .param("correo", "invalid@example.com")
                        .param("contrasena", "wrongpass")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andDo(print())
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Error interno del servidor (desde TestAdvice): Credenciales inválidas"));
    }

    @Test
    void login_cuandoServicioLanzaExcepcionInterna_debeRetornarErrorYStatus500() throws Exception {
        when(usuarioService.autenticarUsuario(anyString(), anyString()))
                .thenThrow(new RuntimeException("Error inesperado del servicio de autenticación"));

        mockMvc.perform(post("/auth/login")
                        .param("correo", testCorreo)
                        .param("contrasena", testContrasena)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andDo(print())
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Error interno del servidor (desde TestAdvice): Error inesperado del servicio de autenticación"));
    }

    @ControllerAdvice
    static class TestAuthControllerExceptionHandlerAdvice {
        @ExceptionHandler(RuntimeException.class)
        @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
        public ResponseEntity<String> handleRuntimeException(RuntimeException ex) {
            return new ResponseEntity<>("Error  de autenticación (desde TestAdvice): " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}