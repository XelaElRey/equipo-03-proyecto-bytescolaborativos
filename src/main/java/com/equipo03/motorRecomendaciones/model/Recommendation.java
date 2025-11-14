package com.equipo03.motorRecomendaciones.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;
import java.util.*;

@Entity
@Table(name = "recommendations")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
/*
 * Entidad que representa una recomendación para un usuario, generada por el sistema de recomendaciones.
 */
public class Recommendation {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

   
    @Column(nullable = false)
    private Instant computedAt = Instant.now();

    @Column(nullable = false)
    private String algorithmVersion;

    //Relación N:1 con User
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    //Relación N:N con Prdouct mediante una tabla intermedia
    @ManyToMany
    @JoinTable(
        name = "product_recommendations",
        joinColumns = @JoinColumn(name = "recommendation_id"),
        inverseJoinColumns = @JoinColumn(name = "product_id")
    )
    private List<Product> products = new ArrayList<>();

}
