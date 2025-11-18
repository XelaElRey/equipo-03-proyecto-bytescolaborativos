package com.equipo03.motorRecomendaciones.model.enums;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Estados posibles de un torneo")
public enum TournamentStatus {

    @Schema(description = "Inscripciones abiertas")
    OPEN,

    @Schema(description = "Inscripciones cerradas")
    CLOSED,

    @Schema(description = "Torneo finalizado")
    FINISHED
}