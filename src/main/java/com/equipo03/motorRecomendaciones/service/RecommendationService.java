package com.equipo03.motorRecomendaciones.service;

import java.util.List;
import java.util.UUID;
import com.equipo03.motorRecomendaciones.dto.ProductDTO;

public interface RecommendationService {

    List<ProductDTO> getRecommendations(UUID userId);

}
