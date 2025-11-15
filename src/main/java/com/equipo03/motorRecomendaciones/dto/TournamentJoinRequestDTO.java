package com.equipo03.motorRecomendaciones.dto;

import java.util.UUID;

import lombok.Data;

@Data
public class TournamentJoinRequestDTO {
    private UUID userId;
    private String nickname;
}
