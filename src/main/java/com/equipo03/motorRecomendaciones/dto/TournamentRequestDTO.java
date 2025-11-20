package com.equipo03.motorRecomendaciones.dto;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import lombok.Data;

@Data
@Schema(description = "Datos necesarios para crear un torneo")
public class TournamentRequestDTO {

    @Schema(description = "Nombre del torneo", example = "Torneo Pro League 2025")
    @NotBlank(message = "El nombre es obligatorio")
    private String name;

    @Schema(description = "Juego al que pertenece el torneo", example = "League of Legends")
    private String game;

    @Schema(description = "Fecha y hora en que inicia el torneo (debe ser futura)", example = "2026-03-01T18:00:00")
    @NotNull(message = "La fecha de inicio es obligatoria")
    @Future(message = "La fecha de inicio debe ser futura")
    private LocalDateTime startDate;

    @Schema(description = "Fecha y hora en que finaliza el torneo (debe ser futura)", example = "2026-03-03T20:00:00")
    @NotNull(message = "La fecha de fin es obligatoria")
    @Future(message = "La fecha de fin debe ser futura")
    private LocalDateTime endDate;

    @Schema(description = "Fecha y hora en que se habilitan las inscripciones (debe ser futura)", example = "2026-04-15T00:00:00")
    @NotNull(message = "La fecha de inicio de inscripción es obligatoria")
    @Future(message = "La fecha de inicio de inscripción debe ser futura")
    private LocalDateTime registrationOpenAt;

    @Schema(description = "Fecha y hora en que se cierran las inscripciones (debe ser futura)", example = "2026-04-28T23:59:59")
    @NotNull(message = "La fecha de cierre de inscripción es obligatoria")
    @Future(message = "La fecha de cierre de inscripción debe ser futura")
    private LocalDateTime registrationCloseAt;

    @Schema(description = "Cantidad máxima de participantes permitidos en el torneo. Si es null, el torneo no tiene límite.", example = "32")
    private Integer maxParticipants;

    @Schema(description = "Reglas o información adicional sobre el torneo", example = "Cada partida será al mejor de 3.")
    private String rules;
}
