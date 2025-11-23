package com.equipo03.motorRecomendaciones.dto;

import java.util.UUID;
import lombok.Data;

@Data
public class RatingRequestDTO {
    private UUID userId;
    private UUID productId;
    private int score;
}
