package com.equipo03.motorRecomendaciones.model;

import java.time.Instant;
import java.util.*;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "ratings")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
/**
 * Entidad que representa una calificación (rating) que un usuario otorga a un producto.
 */
public class Rating {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private int score;

    @Column(nullable = false, updatable = false)
    private final Instant createdAt = Instant.now();

    //Relacion N:1 con User
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    //Relación N:1 con Product
    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

}
