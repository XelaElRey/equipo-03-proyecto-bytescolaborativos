package com.equipo03.motorRecomendaciones.controller;

import com.equipo03.motorRecomendaciones.dto.JwtResponseDTO;
import com.equipo03.motorRecomendaciones.dto.LoginRequestDTO;
import com.equipo03.motorRecomendaciones.dto.RegisterRequestDTO;
import com.equipo03.motorRecomendaciones.model.User;
import com.equipo03.motorRecomendaciones.service.UserService;
import com.equipo03.motorRecomendaciones.config.JwtUtil;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserService userService;

    public AuthController(AuthenticationManager authenticationManager, JwtUtil jwtUtil, UserService userService) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<JwtResponseDTO> login(@RequestBody LoginRequestDTO loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()));

        String token = jwtUtil.generateToken(authentication);
        return ResponseEntity.ok(new JwtResponseDTO(token));
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequestDTO request) {
        if (userService.existsByUsername(request.getUsername())) {
            return ResponseEntity.badRequest().body("El nombre de usuario ya está en uso.");
        }

        User user = userService.registerUser(request.getUsername(), request.getEmail(), request.getPassword());
        return ResponseEntity.ok("Usuario registrado correctamente con ID: " + user.getId());
    }
}