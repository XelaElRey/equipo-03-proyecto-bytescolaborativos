package com.equipo03.motorRecomendaciones.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import lombok.Data;

@Data
public class TournamentRequestDTO {
    @NotBlank
    private String name;

    private String game;

    @NotNull
    private LocalDateTime startDate;

    @NotNull
    private LocalDateTime endDate;

    @NotNull
    private LocalDateTime registrationOpenAt;

    @NotNull
    private LocalDateTime registrationCloseAt;

    private Integer maxParticipants;

    private String rules;

}
