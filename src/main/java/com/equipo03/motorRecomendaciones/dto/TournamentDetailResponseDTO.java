package com.equipo03.motorRecomendaciones.dto;

import lombok.Data;

@Data
public class TournamentDetailResponseDTO {
    private Long id;
    private String name;
    private String status;
    private String rules;
    private Integer maxParticipants;
    // private List<String> participants;
}
