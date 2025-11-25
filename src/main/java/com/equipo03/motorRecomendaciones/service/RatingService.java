package com.equipo03.motorRecomendaciones.service;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.equipo03.motorRecomendaciones.exception.ResourceNotFoundException;
import com.equipo03.motorRecomendaciones.mapper.RatingMapper;
import com.equipo03.motorRecomendaciones.dto.RatingResponseDTO;
import com.equipo03.motorRecomendaciones.exception.BadRequestException;
import com.equipo03.motorRecomendaciones.model.Product;
import com.equipo03.motorRecomendaciones.model.Rating;
import com.equipo03.motorRecomendaciones.model.User;
import com.equipo03.motorRecomendaciones.repository.ProductRepository;
import com.equipo03.motorRecomendaciones.repository.RatingRepository;
import com.equipo03.motorRecomendaciones.repository.UserRepository;

import jakarta.transaction.Transactional;

@Service
public class RatingService {

    @Autowired
    private RatingRepository ratingRepository;

    @Autowired
    private UserRepository userRepository; // para verificacion de que existe el usuario

    @Autowired
    private ProductRepository productRepository; // para verificacion de que existe el usuario

    @Autowired
    private RatingMapper ratingMapper;

    // Clasificación del producto . PASO EL ID POR PARAMETRO HASTA QUE ESTE LA
    // SEGURIDAD EN EL MAIN
    // Además le agrego la anotacion @Transactional para que al crear un nuevo
    // Rating y recalcular el promedio sea atomico
    @Transactional
    public RatingResponseDTO setRating(String username, UUID productId, int score) {

        if (score < 1 || score > 5) {
            throw new BadRequestException("La valoración tiene que estar entre 1 y 5");
        }

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("El usuario no existe"));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("El producto no existe"));

        if (ratingRepository.existsByUserIdAndProductId(user.getId(), productId)) {
            throw new BadRequestException("El usuario ya valoro este producto");
        }

        Rating newRating = Rating.builder()
                .score(score)
                .user(user)
                .product(product)
                .build();
        Rating savedRating = ratingRepository.save(newRating);

        // actualizo el score del producto
        calculateAverage(productId);

        return ratingMapper.toResponseDTO(savedRating);
    }

    // calcular promedio de ratings
    public Long calculateAverage(UUID productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("El producto no existe"));
        double average = ratingRepository.findByProductId(productId).stream()
                .mapToInt(Rating::getScore)
                .average()
                .orElse(0);

        Long result = Math.round(average);
        product.setPopularityScore(result);
        productRepository.save(product);
        return result;
    }
}
