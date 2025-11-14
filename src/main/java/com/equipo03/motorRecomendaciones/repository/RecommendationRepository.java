package com.equipo03.motorRecomendaciones.repository;

import java.util.UUID;
import com.equipo03.motorRecomendaciones.model.Recommendation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;


@Repository
public interface RecommendationRepository extends JpaRepository<Recommendation, UUID>{
    //Obtener todas las recomendaciones para un usuario
    List<Recommendation> findByUserId(UUID userId);
}
