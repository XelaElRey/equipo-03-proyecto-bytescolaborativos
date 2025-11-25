package com.equipo03.motorRecomendaciones.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import com.equipo03.motorRecomendaciones.model.Rating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface RatingRepository extends JpaRepository<Rating, UUID> {
    // Obtener todas las valoraciones de un usuario
    List<Rating> findByUserId(UUID userId);

    // Obtener todas las valoraciones de un producto
    List<Rating> findByProductId(UUID productId);

    // Calcular el rating promedio por producto para una lista de productos
    @Query("SELECT r.product.id, AVG(r.score) " +
           "FROM Rating r " +
           "WHERE r.product.id IN :ids " +
           "GROUP BY r.product.id")
    List<Object[]> obtenerPromedioRatingPorIdsProductos(@Param("ids") List<UUID> idsProductos);

    // Calcular la cantidad de valoraciones (popularidad) por producto para una lista de productos
    @Query("SELECT r.product.id, COUNT(r) " +
           "FROM Rating r " +
           "WHERE r.product.id IN :ids " +
           "GROUP BY r.product.id")
    List<Object[]> obtenerCantidadValoracionesPorIdsProductos(@Param("ids") List<UUID> idsProductos);

}
