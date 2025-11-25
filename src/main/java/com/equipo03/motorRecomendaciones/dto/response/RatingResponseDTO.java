package com.equipo03.motorRecomendaciones.dto.response;

import java.time.Instant;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor

public class RatingResponseDTO {
    private UUID id;
    private int score;
    private Instant createdAt;
    private UUID userId;
    private UUID productId;
}