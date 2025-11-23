package com.equipo03.motorRecomendaciones.controller;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import com.equipo03.motorRecomendaciones.dto.UserDTO;
import com.equipo03.motorRecomendaciones.dto.request.LoginRequestDto;
import com.equipo03.motorRecomendaciones.dto.request.UserRequestDto;
import jakarta.validation.Valid;

public interface UserController {
    

    /**
     * Endpoint para registrar un nuevo usuario
     * @param UserRequestDto
     * 
     */

    public ResponseEntity<String> registerUser(@Valid @RequestBody UserRequestDto userRequestDto);


    /**
     * Endpoint para inciar sesión con un usuario válido.
     * @param LoginRequestDto
     */

    public ResponseEntity<String> loginUser(@Valid @RequestBody LoginRequestDto loginRequest);

    /**
     * Endpoint para obtener todos los usuarios del sistema
     */

    public ResponseEntity<List<UserDTO>> getAllUsers();


    /**
     * Endpoint para eliminar un usuario según su identificador 
     */

    public ResponseEntity<Void> deleteUser(@PathVariable String id);


    /**
     * Endpoint para obtener un usuario según su email
     * 
     */
    ResponseEntity<UserDTO> getUserByEmail(String email);



    


}   
