package com.equipo03.motorRecomendaciones.controller.impl;

import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.equipo03.motorRecomendaciones.controller.RecomendationController;
import com.equipo03.motorRecomendaciones.dto.ProductDTO;
import com.equipo03.motorRecomendaciones.service.RecommendationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@RestController
@RequestMapping("/recommendations")
@RequiredArgsConstructor
public class RecomendationControllerImpl implements RecomendationController {

    private final RecommendationService recomendationService;

    /**
     * Endpoint GET que devuelve la lista de productos recomendados para un usuario .
     * 
     * @param userId ID del usuario
     * @return Lista de productos recomendados (ProductDTO)
     */

    @GetMapping("/{userId}") 
    @Override
    public ResponseEntity<List<ProductDTO>> getRecommendations(@PathVariable UUID userId) {
        //Llama al servicio que combina tags, ratings, y popularidad
        //Guarda la recomendación en la tabla Recommendation y devuelve los resultados
        List<ProductDTO> recomendaciones = recomendationService.getRecommendations(userId);
        return ResponseEntity.ok(recomendaciones);
    }

}
