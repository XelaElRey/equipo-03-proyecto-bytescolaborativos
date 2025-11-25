package com.equipo03.motorRecomendaciones.dto.request;

import java.util.UUID;
import lombok.Data;

@Data
public class RatingRequestDTO {
    private UUID productId;
    private int score;
}
