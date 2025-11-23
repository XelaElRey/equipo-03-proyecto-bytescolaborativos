package com.equipo03.motorRecomendaciones.service;

import java.util.List;
import java.util.UUID;

import com.equipo03.motorRecomendaciones.dto.UserDTO;
import com.equipo03.motorRecomendaciones.dto.request.LoginRequestDto;
import com.equipo03.motorRecomendaciones.dto.request.UserRequestDto;
import com.equipo03.motorRecomendaciones.model.User;


/**
 * Interfaz que define los métodos del servicio de usuario.
 * @author Alex
 */
public interface UserService {

    User findByUsername(String username);

    User findByEmail(String email);

    /**
     * Método encargado de registrar un nuevo usuario en el sistema
     * @param userRequestDTO
     * @return JWT token generado para el usuario registrado
     */
    String registerUser(UserRequestDto userRequestDTO);


    /**
     * Método encargado de autenticar a un usuario en el sistema
     * @param loginRequestDto
     * @return JWT token si la autenticación es exitosa
     * 
     */
    String loginUser(LoginRequestDto loginRequestDto);


    /**
     * Método encargado de registrar un usuario administrador en el sistema
     * @return
     */
    String registerAdmin(UserRequestDto userRequestDto);

    /**
     * Método encargado de obtener todos los usuarios del sistema
     * @return lista de usuarios
     */

    List<UserDTO> getAllUsers();


    /**
     * Método encargado de eliminar un usuario según su id
     */
    void deleteUser(UUID userId);


    /**
     * Método encargado de devolveer un UserDTO a partir de su email.
     * @param email
     * @return
     */
    UserDTO getUserByEmail(String email);
}
