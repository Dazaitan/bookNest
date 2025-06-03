package com.eCommers.bookNest.services;

import com.eCommers.bookNest.config.jwt.JwtService;
import com.eCommers.bookNest.entity.Rol;
import com.eCommers.bookNest.entity.Usuario;
import com.eCommers.bookNest.repository.UsuarioRepository;
import com.eCommers.bookNest.util.PasswordService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordService passwordService;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private UsuarioService usuarioService;

    private Usuario usuarioMock;

    @BeforeEach
    void setUp() {
        usuarioMock = new Usuario();
        usuarioMock.setId(1L);
        usuarioMock.setCorreo("test@example.com");
        usuarioMock.setContrasena("hashedPassword");
        usuarioMock.setRol(Rol.CLIENTE);
        usuarioMock.setFechaRegistro(LocalDateTime.now());
    }

    @Test
    void registrarUsuario_DeberiaGuardarUsuarioConContraseñaEncriptada() {
        when(passwordService.encriptarContrasena(anyString())).thenReturn("hashedPassword");
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioMock);

        Usuario usuarioRegistrado = usuarioService.registrarUsuario(usuarioMock);

        assertNotNull(usuarioRegistrado);
        assertEquals("hashedPassword", usuarioRegistrado.getContrasena());
        verify(usuarioRepository, times(1)).save(usuarioMock);
    }

    @Test
    void obtenerUsuarioPorCorreo_DeberiaRetornarUsuarioSiExiste() {
        when(usuarioRepository.findByCorreo("test@example.com")).thenReturn(Optional.of(usuarioMock));

        Usuario usuarioEncontrado = usuarioService.obtenerUsuarioPorCorreo("test@example.com");

        assertNotNull(usuarioEncontrado);
        assertEquals(usuarioMock.getCorreo(), usuarioEncontrado.getCorreo());
    }

    @Test
    void obtenerUsuarioPorCorreo_DeberiaLanzarExcepcionSiNoExiste() {
        when(usuarioRepository.findByCorreo("test@example.com")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> usuarioService.obtenerUsuarioPorCorreo("test@example.com"));
    }

    @Test
    void autenticarUsuario_DeberiaRetornarTokenSiCredencialesSonCorrectas() {
        when(usuarioRepository.findByCorreo("test@example.com")).thenReturn(Optional.of(usuarioMock));
        when(passwordService.verificarContrasena(anyString(), anyString())).thenReturn(true);
        when(jwtService.generarToken(anyString(), anyString())).thenReturn("mockedToken");

        String token = usuarioService.autenticarUsuario("test@example.com", "hashedPassword");

        assertNotNull(token);
        assertEquals("mockedToken", token);
    }

    @Test
    void autenticarUsuario_DeberiaLanzarExcepcionSiCorreoNoExiste() {
        when(usuarioRepository.findByCorreo("test@example.com")).thenReturn(Optional.empty());

        assertThrows(BadCredentialsException.class, () -> usuarioService.autenticarUsuario("test@example.com", "hashedPassword"));
    }

    @Test
    void autenticarUsuario_DeberiaLanzarExcepcionSiContraseñaEsIncorrecta() {
        when(usuarioRepository.findByCorreo("test@example.com")).thenReturn(Optional.of(usuarioMock));
        when(passwordService.verificarContrasena(anyString(), anyString())).thenReturn(false);

        assertThrows(BadCredentialsException.class, () -> usuarioService.autenticarUsuario("test@example.com", "wrongPassword"));
    }
}
