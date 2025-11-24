package com.equipo03.motorRecomendaciones.service.impl;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Service;

import com.equipo03.motorRecomendaciones.dto.ProductDTO;
import com.equipo03.motorRecomendaciones.mapper.ProductMapper;
import com.equipo03.motorRecomendaciones.model.Product;
import com.equipo03.motorRecomendaciones.model.Rating;
import com.equipo03.motorRecomendaciones.model.Recommendation;
import com.equipo03.motorRecomendaciones.model.User;
import com.equipo03.motorRecomendaciones.repository.ProductRepository;
import com.equipo03.motorRecomendaciones.repository.RatingRepository;
import com.equipo03.motorRecomendaciones.repository.RecommendationRepository;
import com.equipo03.motorRecomendaciones.repository.UserRepository;
import com.equipo03.motorRecomendaciones.service.RecommendationService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RecomendationServiceImpl implements RecommendationService {

    private final UserRepository userRepository;
    private final RatingRepository ratingRepository;
    private final ProductRepository productRepository;
    private final RecommendationRepository recommendationRepository;
    private final ProductMapper productMapper;
    
    @Override
    public List<ProductDTO> getRecommendations(UUID userId) {
        // 1️⃣ Obtener usuario
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // 2️⃣ Obtener ratings del usuario
        List<Rating> userRatings = ratingRepository.findByUserId(userId);

        // 3️⃣ Recopilar todos los tags de productos valorados
        Set<String> userTags = userRatings.stream()
                .flatMap(rating -> rating.getProduct().getTags().stream())
                .collect(Collectors.toSet());

        // 4️⃣ Filtrar productos no valorados por el usuario
        List<Product> candidateProducts = productRepository.findAll()
                .stream()
                .filter(p -> userRatings.stream()
                        .noneMatch(r -> r.getProduct().getId().equals(p.getId())))
                .collect(Collectors.toList());

        // 5️⃣ Calcular score de recomendación
        Map<Product, Double> scoredProducts = new HashMap<>();
        for (Product product : candidateProducts) {
            // Tags en común
            long matchingTags = product.getTags().stream()
                    .filter(userTags::contains)
                    .count();

            // Promedio de rating
            double averageRating = product.getRatings().stream()
                    .mapToInt(Rating::getScore)
                    .average()
                    .orElse(0.0);

            // Score ponderado (70% tags, 30% rating)
            double score = matchingTags * 0.7 + averageRating * 0.3;
            scoredProducts.put(product, score);
        }

        // 6️⃣ Ordenar productos por score descendente
        List<ProductDTO> recommendations = scoredProducts.entrySet().stream()
                .sorted(Map.Entry.<Product, Double>comparingByValue().reversed())
                .map(entry -> ProductMapper.INSTANCE.toDTO(entry.getKey()))
                .collect(Collectors.toList());

        // 7️⃣ Guardar en la tabla Recommendation
        if (!recommendations.isEmpty()) {
            Recommendation recommendation = new Recommendation();
            recommendation.setUser(user);
            recommendation.setAlgorithmVersion("v1.0");
            recommendation.setComputedAt(LocalDateTime.now());
            recommendation.setProducts(recommendations.stream()
                    .map(dto -> productRepository.getReferenceById(dto.getId()))
                    .collect(Collectors.toList()));
            recommendationRepository.save(recommendation);
        }

        // 8️⃣ Retornar recomendaciones
        return recommendations;
    }
}
