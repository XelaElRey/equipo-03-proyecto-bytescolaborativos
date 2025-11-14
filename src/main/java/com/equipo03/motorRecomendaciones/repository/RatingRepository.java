package com.equipo03.motorRecomendaciones.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import com.equipo03.motorRecomendaciones.model.Rating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RatingRepository extends JpaRepository<Rating, UUID>{
    //Obtener todas las valoraciones de un usuario
    List<Rating> findByUserId(UUID userId);
    //Obtener todas las valoraciones de un producto
    List<Rating> findByProductId(UUID productId);

}
