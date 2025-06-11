package com.eCommers.bookNest.controller;

import com.eCommers.bookNest.config.filters.JwtAuthenticationFilter;
import com.eCommers.bookNest.entity.Usuario;
import com.eCommers.bookNest.services.UsuarioService;
import com.eCommers.bookNest.util.PasswordService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
// --- CAMBIO CLAVE AQUÍ: Excluimos la configuración de seguridad ---
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration; // Importación para excluir seguridad
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc; // Para configurar MockMvc sin filtros

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ControllerAdvice
class TestExceptionHandlerAdvice {
    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<String> handleRuntimeException(RuntimeException ex) {
        return new ResponseEntity<>("Error interno del servidor (desde TestAdvice): " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

@WebMvcTest(
        controllers = UsuarioController.class,
        // --- EXCLUIMOS LA AUTOCONFIGURACIÓN DE SEGURIDAD ---
        excludeAutoConfiguration = {SecurityAutoConfiguration.class},
        // --- Y EXCLUIMOS TU FILTRO JWT (SI ESTÁ MARCADO COMO @Component y lo detectaría Spring) ---
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = {JwtAuthenticationFilter.class}
        )
)
@AutoConfigureMockMvc(addFilters = false) // desactivar todos los filtros de MockMvc
class UsuarioControllerWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UsuarioService usuarioService;

    private Usuario testUsuario;


    @BeforeEach
    void setUp() {
        testUsuario = new Usuario();
        testUsuario.setId(1L);
        testUsuario.setCorreo("test@example.com");
        testUsuario.setNombre("Test User");
        testUsuario.setContrasena("hashedpassword");
    }

    @Test
    void registrarUsuario_debeRetornarNuevoUsuarioYStatus200() throws Exception {
        Usuario usuarioEntrada = new Usuario();
        usuarioEntrada.setCorreo("nuevo@example.com");
        usuarioEntrada.setNombre("Nuevo Usuario");
        usuarioEntrada.setContrasena("rawpassword123");

        when(usuarioService.registrarUsuario(any(Usuario.class))).thenReturn(testUsuario);

        mockMvc.perform(post("/usuarios/registrar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(usuarioEntrada))
                        .with(csrf())) // CSRF sigue siendo necesario para POST/PUT/DELETE
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.correo").value("test@example.com"))
                .andExpect(jsonPath("$.nombre").value("Test User"));
    }

    @Test
    void obtenerUsuario_conCorreoExistente_debeRetornarUsuarioYStatus200() throws Exception {
        String correoExistente = "test@example.com";
        when(usuarioService.obtenerUsuarioPorCorreo(correoExistente)).thenReturn(testUsuario);

        // GET no requiere CSRF. Como la seguridad está desactivada.
        mockMvc.perform(get("/usuarios/{correo}", correoExistente)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.correo").value(correoExistente))
                .andExpect(jsonPath("$.nombre").value("Test User"));
    }

    @Test
    void obtenerUsuario_conCorreoNoExistente_debeRetornarStatus200ConCuerpoVacio() throws Exception {
        String correoNoExistente = "noexiste@example.com";
        when(usuarioService.obtenerUsuarioPorCorreo(correoNoExistente)).thenReturn(null);

        mockMvc.perform(get("/usuarios/{correo}", correoNoExistente)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").doesNotExist());
    }

    @Test
    void registrarUsuario_cuandoServicioLanzaExcepcion_debeRetornarErrorAdecuado() throws Exception {
        Usuario usuarioEntrada = new Usuario();
        usuarioEntrada.setCorreo("duplicado@example.com");

        when(usuarioService.registrarUsuario(any(Usuario.class)))
                .thenThrow(new RuntimeException("El correo 'duplicado@example.com' ya existe."));

        mockMvc.perform(post("/usuarios/registrar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(usuarioEntrada))
                        .with(csrf())) // CSRF sigue siendo necesario para POST
                .andDo(print()) // <-- ¡Añade esta línea para ver la salida completa!
                .andExpect(status().isInternalServerError());
    }
}

