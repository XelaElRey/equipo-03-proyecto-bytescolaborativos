package com.equipo03.motorRecomendaciones.dto;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Schema(description = "Detalle completo de un torneo, incluyendo participantes")
public class TournamentDetailResponseDTO {

    @Schema(description = "ID del torneo", example = "32")
    private Long id;

    @Schema(description = "Nombre del torneo", example = "Champions League eSports")
    private String name;

    @Schema(description = "Estado actual del torneo", example = "OPEN")
    private String status;

    @Schema(description = "Reglas generales o información relevante", example = "Partidas BO3. Se requiere puntualidad estricta.")
    private String rules;

    @Schema(description = "Lista de nombres o nicknames de los participantes")
    private List<String> participants;

    @Schema(description = "Cantidad máxima de participantes permitida", example = "64")
    private Integer maxParticipants;
}
