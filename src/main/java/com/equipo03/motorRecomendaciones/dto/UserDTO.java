package com.equipo03.motorRecomendaciones.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import java.util.UUID;

@Data
@Builder
@ToString
@Schema(description = "DTO que representa la información de un usuario sin exponer la contraseña")
public class UserDTO {

    @Schema(description = "Identificador único del usuario")
    @NotNull
    private UUID id;

    @Schema(description = "Nombre de usuario")
    @NotBlank
    @Size(min = 3, max = 50)
    private String username;

    @Schema(description = "Correo electrónico del usuario")
    @Email
    @NotBlank
    private String email;

    @Schema(description = "Rol del usuario en el sistema")
    @NotNull
    private String role;

    @Schema(description = "Indica si el usuario está activo")
    @NotNull    
    private boolean active;
}
