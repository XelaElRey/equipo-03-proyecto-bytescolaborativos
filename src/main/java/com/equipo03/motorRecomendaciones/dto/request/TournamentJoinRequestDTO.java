package com.equipo03.motorRecomendaciones.dto.request;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Datos para unirse a un torneo")
public class TournamentJoinRequestDTO {

    @Schema(description = "Nombre o apodo del jugador dentro del torneo (opcional)", example = "DevChPlayer")
    private String nickname;
}