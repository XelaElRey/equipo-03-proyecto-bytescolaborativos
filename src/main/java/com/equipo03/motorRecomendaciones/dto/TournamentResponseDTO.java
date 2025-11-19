package com.equipo03.motorRecomendaciones.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Representa un torneo en listados generales")
public class TournamentResponseDTO {

    @Schema(description = "ID del torneo", example = "12")
    private Long id;

    @Schema(description = "Nombre del torneo", example = "Torneo Pro League 2025")
    private String name;

    @Schema(description = "Juego del torneo", example = "League of Legends")
    private String game;

    @Schema(description = "Estado actual del torneo", example = "UPCOMING")
    private String status;

    @Schema(description = "Cantidad de participantes inscritos", example = "18")
    private int participants;
}