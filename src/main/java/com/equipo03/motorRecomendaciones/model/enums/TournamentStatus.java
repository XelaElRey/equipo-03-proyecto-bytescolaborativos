package com.equipo03.motorRecomendaciones.model.enums;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Estados posibles de un torneo")
public enum TournamentStatus {

    @Schema(description = "Torneo programado")
    UPCOMING,

    @Schema(description = "Inscripciones abiertas")
    OPEN,

    @Schema(description = "Inscripciones cerradas")
    CLOSED,
}