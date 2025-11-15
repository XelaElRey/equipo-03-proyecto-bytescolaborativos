package com.equipo03.motorRecomendaciones.dto;

import java.util.UUID;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TournamentJoinResponseDTO {
    private String message;
    private Long tournamentId;
    private UUID userId;
    private String status;
}
