package com.equipo03.motorRecomendaciones.controller.impl;

import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.equipo03.motorRecomendaciones.dto.UserDTO;
import com.equipo03.motorRecomendaciones.dto.request.LoginRequestDto;
import com.equipo03.motorRecomendaciones.dto.request.UserRequestDto;
import com.equipo03.motorRecomendaciones.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/api/auth")
public class UserControllerImpl {

    private final UserService userService;

    public UserControllerImpl(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    @ApiResponse
    @Operation(summary = "Registra un usuario", description = "Registra un nuevo usuario en el sistema y devuelve un token JWT asociado a este usuario")
    public ResponseEntity<String> registerUser(@Valid @RequestBody UserRequestDto userRequestDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.registerUser(userRequestDto));
    }

    /**
     * Endpoint para inciar sesión con un usuario
     * 
     * @param loginRequestDto información del usuario
     * @return devuelve un mensaje indicando el resultado del inicio de sesión
     */

    @PostMapping("/login")
    @ApiResponse
    @Operation(summary = "Iniciar sesión de un usuario", description = "Inicia sesión de un usuario con los datos proporcionados y devuelve un token JWT del usuario")

    public ResponseEntity<String> loginUser(@Valid @RequestBody LoginRequestDto loginRequest) {
        return ResponseEntity.ok(userService.loginUser(loginRequest));
    }

    /**
     * Endpoint para registar un administrador
     * 
     */
    @ApiResponse
    @Operation(summary = "Registrar un usuario admin", description = "Registrar un usuario administrador y devolver su token JWT")
    @PostMapping("/register/admin")
    public ResponseEntity<String> registerAdmin(@Valid @RequestBody UserRequestDto userRequestDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.registerAdmin(userRequestDto));
    }

    /**
     * Endpoint para obtener la información de todos los usuarios del sistema
     */
    @GetMapping("/users")
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    /**
     * Endpoint para obtener un usuario según su email
     * 
     */

    @GetMapping("/user/{email}")
    public ResponseEntity<UserDTO> getUserByEmail(@PathVariable String email) {
        return ResponseEntity.ok(userService.getUserByEmail(email));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUser(String id) {
        UUID uuid = UUID.fromString(id);
        userService.deleteUser(uuid);
        return ResponseEntity.noContent().build();
    }
}
