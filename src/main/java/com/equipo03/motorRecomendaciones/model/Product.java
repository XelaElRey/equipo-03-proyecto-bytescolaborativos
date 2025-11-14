package com.equipo03.motorRecomendaciones.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.*;

@Entity
@Table(name = "products")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
/**
 * Entidad que representa un producto en el sistema de recomendaciones
 */
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private String description;
    @Column(nullable = false)
    private String category;

    @ElementCollection
    private Set<String> tags;

    @Column(nullable = false)
    private Long popularityScore = 0L;

    // Relación 1:N con Rating
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Rating> ratings = new ArrayList<>();
    //Relación N:N con Recommendation
    @ManyToMany(mappedBy = "products")
    private List<Recommendation> recommendations = new ArrayList<>();

}
