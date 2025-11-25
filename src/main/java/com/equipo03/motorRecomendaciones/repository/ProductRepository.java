package com.equipo03.motorRecomendaciones.repository;

import java.util.Optional;
import com.equipo03.motorRecomendaciones.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface ProductRepository extends JpaRepository<Product, UUID> {

    Optional<Product> findByName(String name);

}
