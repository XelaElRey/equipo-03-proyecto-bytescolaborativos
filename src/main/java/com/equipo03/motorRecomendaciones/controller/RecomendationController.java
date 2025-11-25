package com.equipo03.motorRecomendaciones.controller;

import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import com.equipo03.motorRecomendaciones.dto.ProductDTO;

public interface RecomendationController {

        public ResponseEntity<List<ProductDTO>> getRecommendations(@PathVariable UUID userId);
}