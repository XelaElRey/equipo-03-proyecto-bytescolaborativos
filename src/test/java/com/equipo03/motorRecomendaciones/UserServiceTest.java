package com.equipo03.motorRecomendaciones;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.util.Optional;
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
import com.equipo03.motorRecomendaciones.dto.request.UserRequestDto;
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
    private PasswordEncoder passwordEncoder;
    @InjectMocks
    private UserServiceImpl userService;

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

}
