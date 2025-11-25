package com.equipo03.motorRecomendaciones.service.impl;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.equipo03.motorRecomendaciones.config.JwtUtil;
import com.equipo03.motorRecomendaciones.dto.UserDTO;
import com.equipo03.motorRecomendaciones.dto.request.LoginRequestDto;
import com.equipo03.motorRecomendaciones.dto.request.UserRequestDto;
import com.equipo03.motorRecomendaciones.exception.BadRequestException;
import com.equipo03.motorRecomendaciones.exception.ResourceNotFoundException;
import com.equipo03.motorRecomendaciones.mapper.UserMapper;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import com.equipo03.motorRecomendaciones.model.User;
import com.equipo03.motorRecomendaciones.repository.UserRepository;
import com.equipo03.motorRecomendaciones.service.UserService;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

  private final UserRepository userRepository;
  private final UserMapper userMapper;
  private final JwtUtil jwtUtil;
  private final PasswordEncoder passwordEncoder;
  private final AuthenticationManager authenticationManager;
  private Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

  /**
   * Registrar un nuevo usuario en el sistema
   * 
   * @param UserRequestDto
   * @return token JWT
   */
  @Transactional
  public String registerUser(UserRequestDto userRequestDto) {
    verificarRegistro(userRequestDto.getEmail(), userRequestDto.getUsername());

    UserDTO newUserDTO = userMapper.defaultUserDTO(userRequestDto);

    User user = userMapper.userDtoToUser(newUserDTO);

    user.setPassword(passwordEncoder.encode(userRequestDto.getPassword()));

    userRepository.save(user);

    logger.info("Usuario registrado: {} ", user.getEmail());

    Authentication authentication = authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(
            user.getUsername(),
            userRequestDto.getPassword() // la contraseña en texto plano
        ));
    return jwtUtil.generateToken(authentication);

  }

  /**
   * Registrar un nuevo usuario ADMIN en el sistema
   * 
   * @param UserRequestDto
   * @return token JWT
   */
  public String registerAdmin(UserRequestDto userRequestDto) {
    verificarRegistro(userRequestDto.getEmail(), userRequestDto.getUsername());

    UserDTO newUserDto = userMapper.defaultUserDTO(userRequestDto);

    User user = userMapper.userDtoToUser(newUserDto);

    user.setPassword(passwordEncoder.encode(userRequestDto.getPassword()));
    userRepository.save(user);

    logger.info("Usuario registrado: {} ", user.getEmail());

    Authentication authentication = authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(
            user.getUsername(),
            userRequestDto.getPassword() // la contraseña en texto plano
        ));
    return jwtUtil.generateToken(authentication);

  }

  /**
   * Método encargado de generar un objeto Authentication
   * 
   * @param username
   * @param password
   * @return
   */
  public Authentication buildAuthentication(String username, String password) {
    return new UsernamePasswordAuthenticationToken(username, password);
  }

  /**
   * Método encargado de verificar el registro, controlando que el email
   * introducido no exista previamente en la BD
   * 
   * @param email
   * @param username
   */
  public void verificarRegistro(String email, String username) {
    if (userRepository.findByEmail(email).isPresent()) {
      throw new BadRequestException("El email ya existe");
    }
  }

  /**
   * Método encargado de obtener un usuario por su nombre de usuario
   * 
   * @param Username
   */
  @Override
  public User findByUsername(String username) {
    Optional<User> user = userRepository.findByUsername(username);
    if (user.isEmpty()) {
      throw new ResourceNotFoundException(username);
    }
    return user.get();
  }

  /**
   * Método encargad de obtener un usuario por su email
   * 
   * @param email
   */
  @Override
  public User findByEmail(String email) {
    Optional<User> user = userRepository.findByEmail(email);
    if (user.isEmpty()) {
      throw new ResourceNotFoundException(email);
    }
    return user.get();
  }

  /**
   * Método encargado de generar un objeto Authentication y devolver un token JWT
   * en caso de que el login sea éxitoso.
   */
  @Override
  public String loginUser(LoginRequestDto loginRequestDto) {
    verificarLogin(loginRequestDto);

    Authentication authentication = authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(
            loginRequestDto.getUsername(),
            loginRequestDto.getPassword()));

    return jwtUtil.generateToken(authentication);
  }

  /**
   * Método encargado de verificar el login, comprobando que la contraseña del dto
   * coincide con la contraseña hasheada del usuario en BD.
   * 
   * @param loginRequestDto
   */
  public void verificarLogin(LoginRequestDto loginRequestDto) {
    User user = findByUsername(loginRequestDto.getUsername());

    if (!passwordEncoder.matches(loginRequestDto.getPassword(), user.getPassword())) {
      throw new BadRequestException("Contraseña invalida");
    }
  }

  /**
   * Método encargado de obtener todos los usuario de la BD
   */
  @Override
  public List<UserDTO> getAllUsers() {
    return userMapper.userListToUserDtoList(userRepository.findAll());
  }

  /**
   * Implementación del método de la interfaz encargado de eliminar un usuario
   * según su UUID
   * 
   * @param userId
   * @throws ResourceNotFoundException
   */
  @Transactional
  public void deleteUser(UUID userId) {
    if (userRepository.existsById(userId)) {
      userRepository.deleteById(userId);
    } else {
      throw new ResourceNotFoundException("Usuario con ID: " + userId + "no encontrado.");
    }
  }

  /**
   * Método encargado de devolver un UserDTO a partir de su email.
   * 
   * @param email
   * @return
   */
  public UserDTO getUserByEmail(String email) {
    User user = findByEmail(email);
    return userMapper.userToUserDTO(user);
  }

}
