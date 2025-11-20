package com.equipo03.motorRecomendaciones.dto;

import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

//CORREGIR, SE IDENTIFICA EL USUARIO POR TOKEN, NO DEBE ENVIAR EL ID EN EL BODY
@Data
@Schema(description = "Datos para unirse a un torneo")
public class TournamentJoinRequestDTO {

    @Schema(description = "ID del usuario que desea unirse al torneo", example = "550e8400-e29b-41d4-a716-446655440000")
    private UUID userId;

    @Schema(description = "Nombre o apodo del jugador dentro del torneo (opcional)", example = "DevChPlayer")
    private String nickname;
}