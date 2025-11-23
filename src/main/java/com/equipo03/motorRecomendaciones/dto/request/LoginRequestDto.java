package com.equipo03.motorRecomendaciones.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
@Builder
@Schema(description = "DTO para la solicitud de inicio de sesión de un usuario")
public class LoginRequestDto {

/**
 * Nombre de usuario para la autenticación
 */
@Schema(description = "Nombre de usuario del usuario", example = "juan123", required = true)
@NotBlank( message = "El nombre de usuario no puede estar vacío")
private String username;

/**
 * Contraseña para la autenticación
 */
@Schema(description = "Contraseña del usuario", example = "Password123!", required = true)
@NotBlank( message = "La contraseña no puede estar vacía")
private String password;


}

