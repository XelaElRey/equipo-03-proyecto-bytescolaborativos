package com.equipo03.motorRecomendaciones.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import static org.mockito.Mockito.*;
import com.equipo03.motorRecomendaciones.config.JwtUtil;
import com.equipo03.motorRecomendaciones.dto.UserDTO;
import com.equipo03.motorRecomendaciones.dto.request.LoginRequestDto;
import com.equipo03.motorRecomendaciones.dto.request.UserRequestDto;
import com.equipo03.motorRecomendaciones.exception.BadRequestException;
import com.equipo03.motorRecomendaciones.exception.ResourceNotFoundException;
import com.equipo03.motorRecomendaciones.mapper.UserMapper;
import com.equipo03.motorRecomendaciones.model.User;
import com.equipo03.motorRecomendaciones.repository.UserRepository;
import com.equipo03.motorRecomendaciones.service.impl.UserServiceImpl;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserMapper userMapper;
    @Mock
    private JwtUtil jwtUtil;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private PasswordEncoder passwordEncoder;
    @InjectMocks
    private UserServiceImpl userService;

    /**
     * Test para comprobar que la funcionalidad para registrar un usuario funciona
     * correctamente
     */
    @Test
    public void testRegisterUser() {

        UserRequestDto userRequestDTO = mock(UserRequestDto.class);
        User userEntity = mock(User.class);
        UserDTO userDTO = mock(UserDTO.class);
        User expectedUser = mock(User.class);

        when(userMapper.defaultUserDTO(userRequestDTO)).thenReturn(userDTO);
        when(userMapper.userDtoToUser(userDTO)).thenReturn(userEntity);
        when(userRepository.save(userEntity)).thenReturn(expectedUser);
        when(jwtUtil.generateToken(any())).thenReturn("mocked-jwt-token");
        String token = userService.registerUser(userRequestDTO);
        verify(userRepository, times(1)).save(userEntity);
        verify(jwtUtil, times(1)).generateToken(any());
        assertEquals("mocked-jwt-token", token);
    }

    @Test
    public void loginUser() {
        // Preparación

        LoginRequestDto loginDto = LoginRequestDto.builder()
                .username("testuser")
                .password("password123")
                .build();

        User mockUser = User.builder()
                .username("testuser")
                .password("encodedPass")
                .build();

        // El método verificarLogin llama a findByUsername --> devolvemos el usuario
        when(userRepository.findByUsername("testuser"))
                .thenReturn(Optional.of(mockUser));

        // El método verificarLogin también comprueba la contraseña
        when(passwordEncoder.matches("password123", "encodedPass"))
                .thenReturn(true);

        // Simulamos que authenticate funciona y devuelve un Authentication mock
        Authentication mockAuth = mock(Authentication.class);
        when(authenticationManager.authenticate(any()))
                .thenReturn(mockAuth);

        // Simulamos que el token se genera correctamente
        when(jwtUtil.generateToken(mockAuth))
                .thenReturn("fake-jwt-login-token");

        // -------- Act ---------
        String token = userService.loginUser(loginDto);

        // ------- Assert -------
        assertNotNull(token);
        assertEquals("fake-jwt-login-token", token);

        // Verifica que se autenticó correctamente
        verify(authenticationManager, times(1)).authenticate(any());

        // Verifica que el token se genera correctamente
        verify(jwtUtil, times(1)).generateToken(mockAuth);

    }

    @Test
    void verificarLogin_noLanzaExcepcion_cuandoPasswordEsCorrecta() {

        LoginRequestDto loginDto = LoginRequestDto.builder()
                .username("testuser")
                .password("pass123")
                .build();

        User mockUser = User.builder()
                .username("testuser")
                .password("encodedPass")
                .build();

        // findByUsername() llamado desde verificarLogin()
        when(userRepository.findByUsername("testuser"))
                .thenReturn(Optional.of(mockUser));

        // Contraseña correcta
        when(passwordEncoder.matches("pass123", "encodedPass"))
                .thenReturn(true);

        assertDoesNotThrow(() -> userService.verificarLogin(loginDto));

        verify(passwordEncoder).matches("pass123", "encodedPass");
    }

    @Test
    void verificarLogin_lanzaExcepcion_cuandoPasswordEsIncorrecta() {

        LoginRequestDto loginDto = LoginRequestDto.builder()
                .username("testuser")
                .password("wrongpass")
                .build();

        User mockUser = User.builder()
                .username("testuser")
                .password("encodedPass")
                .build();

        when(userRepository.findByUsername("testuser"))
                .thenReturn(Optional.of(mockUser));

        when(passwordEncoder.matches("wrongpass", "encodedPass"))
                .thenReturn(false);

        assertThrows(BadRequestException.class,
                () -> userService.verificarLogin(loginDto));

        verify(passwordEncoder).matches("wrongpass", "encodedPass");
    }

    @Test
    void getAllUsers_RetornarListaDeUsuarios() {

        List<User> mockUserList = List.of(
                User.builder().username("u1").email("u1@test.com").build(),
                User.builder().username("u2").email("u2@test.com").build());

        List<UserDTO> mockDTOList = List.of(
                UserDTO.builder().username("u1").email("u1@test.com").build(),
                UserDTO.builder().username("u2").email("u2@test.com").build());

        when(userRepository.findAll()).thenReturn(mockUserList);
        when(userMapper.userListToUserDtoList(mockUserList)).thenReturn(mockDTOList);

        List<UserDTO> result = userService.getAllUsers();

        assertEquals(2, result.size());
        assertEquals("u1", result.get(0).getUsername());

        verify(userRepository).findAll();
        verify(userMapper).userListToUserDtoList(mockUserList);
    }

    @Test
    void deleteUser_eliminaUsuario_cuandoExiste() {

        UUID userId = UUID.randomUUID();

        when(userRepository.existsById(userId)).thenReturn(true);

        assertDoesNotThrow(() -> userService.deleteUser(userId));

        verify(userRepository).deleteById(userId);
    }

    @Test
    void deleteUser_lanzaExcepcion_cuandoNoExiste() {

        UUID userId = UUID.randomUUID();

        when(userRepository.existsById(userId)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class,
                () -> userService.deleteUser(userId));

        verify(userRepository, never()).deleteById(any());
    }

}
