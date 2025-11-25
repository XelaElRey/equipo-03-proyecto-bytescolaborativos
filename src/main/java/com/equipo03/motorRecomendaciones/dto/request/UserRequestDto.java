package com.equipo03.motorRecomendaciones.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
@Builder
@Schema(description = "DTO para la solicitud de registro de un nuevo usuario")
public class UserRequestDto {

    /**
     * El nombre de usuario del nuevo usuario debe tener entre 3 y 50 carácteres y no estar vacío.
     */
    @Schema(description = "Nombre de usuario del nuevo usuario", example = "juan123")
    @NotBlank
    @Size(min = 3, max = 50)
    private String username;


    /**
     * El email del nuevo usuario debe ser válido en formato y no estar vacío
     */
    @Schema(description = "Correo electrónico del nuevo usuario", example = "a@gmail.com")
    @Email
    @NotBlank
    private String email;

    /**
     * La contraseña para el nuevo usuario debe tener entre 6 y 100 carácteres.
     * 
     */

    @Schema(description = "Contraseña del nuevo usuario", example = "Password123!")
    @NotBlank
    @Size(min = 6, max = 100)
    private String password;



}
