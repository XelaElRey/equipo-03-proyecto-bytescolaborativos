package com.equipo03.motorRecomendaciones.dto;

import java.util.List;

import lombok.Data;

@Data
public class TournamentDetailResponseDTO {
    private Long id;
    private String name;
    private String status;
    private String rules;
    private List<String> participants;
    private Integer maxParticipants;
}
