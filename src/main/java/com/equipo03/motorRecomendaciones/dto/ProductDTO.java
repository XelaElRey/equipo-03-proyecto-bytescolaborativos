package com.equipo03.motorRecomendaciones.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.util.Set;
import java.util.UUID;

@Data
@Builder
@Schema(description = "DTO que representa la información de un producto")
public class ProductDTO {

    @Schema(description = "Identificador único del producto")
    private UUID id;

    @Schema(description = "Nombre del producto")
    private String name;

    @Schema(description = "Descripción del producto")
    private String description;

    @Schema(description = "Categoría del producto")
    private String category;

    @Schema(description = "Etiquetas asociadas al producto")
    private Set<String> tags;

    @Schema(description = "Puntuación de popularidad del producto")
    private Long popularityScore;
}
