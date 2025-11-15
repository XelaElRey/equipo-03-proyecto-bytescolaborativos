package com.equipo03.motorRecomendaciones.dto;

import lombok.Data;

@Data
public class TournamentResponseDTO {
    private Long id;
    private String name;
    private String game;
    private String status;
    private int participants;
}