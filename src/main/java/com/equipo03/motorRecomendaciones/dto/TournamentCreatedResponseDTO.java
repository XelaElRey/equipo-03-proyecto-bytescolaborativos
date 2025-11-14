package com.equipo03.motorRecomendaciones.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class TournamentCreatedResponseDTO {
    private Long id;
    private String name;
    private String status;
    private LocalDateTime createdAt;
}
