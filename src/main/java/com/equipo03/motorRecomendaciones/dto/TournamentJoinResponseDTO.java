package com.equipo03.motorRecomendaciones.dto;

import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Respuesta al inscribirse en un torneo")
public class TournamentJoinResponseDTO {

    @Schema(description = "Mensaje informativo sobre el resultado", example = "Inscripción completada")
    private String message;

    @Schema(description = "ID del torneo al que se realizó la inscripción", example = "12")
    private Long tournamentId;

    @Schema(description = "ID del usuario inscrito en el torneo", example = "550e8400-e29b-41d4-a716-446655440000")
    private UUID userId;

    @Schema(description = "Estado actual del torneo tras la inscripción", example = "REGISTERED")
    // CORREGIR PARA QUE DEVUELVA ESE ESTADO
    private String status;
}
