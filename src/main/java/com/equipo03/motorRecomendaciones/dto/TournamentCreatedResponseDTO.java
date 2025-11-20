package com.equipo03.motorRecomendaciones.dto;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Respuesta al crear un torneo exitosamente")
public class TournamentCreatedResponseDTO {

    @Schema(description = "ID del torneo recién creado", example = "15")
    private Long id;

    @Schema(description = "Nombre del torneo creado", example = "Summer Cup 2026")
    private String name;

    @Schema(description = "Estado inicial del torneo", example = "OPEN")
    private String status;

    @Schema(description = "Fecha de creación del torneo", example = "2026-01-10T14:23:11")
    private LocalDateTime createdAt;
}
