package com.equipo03.motorRecomendaciones.dto;

import java.time.Instant;
import java.util.UUID;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RatingResponseDTO {
    private UUID id;
    private int score;
    private Instant createdAt;
    private UUID userId;
    private UUID productId;
}