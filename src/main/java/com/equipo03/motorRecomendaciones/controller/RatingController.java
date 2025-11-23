package com.equipo03.motorRecomendaciones.controller;

import com.equipo03.motorRecomendaciones.dto.RatingRequestDTO;
import com.equipo03.motorRecomendaciones.dto.RatingResponseDTO;
import com.equipo03.motorRecomendaciones.service.RatingService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ratings")
public class RatingController {

    @Autowired
    private RatingService ratingService;

    @Operation(summary = "Crear una valoración", description = "Permite a un usuario valorar un producto con un puntaje entre 1 y 5")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Valoración creada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Error en la solicitud (score inválido o usuario ya valoró)"),
            @ApiResponse(responseCode = "404", description = "Usuario o producto no encontrado")
    })

    @PostMapping
    public ResponseEntity<RatingResponseDTO> createRating(@RequestBody RatingRequestDTO request) {
        RatingResponseDTO response = ratingService.setRating(request.getUserId(), request.getProductId(),
                request.getScore());
        return ResponseEntity.ok(response);
    }

    // PARA VER SI ESTA FUNCIONANDO BIEN EL CALCULO DEL PROMEDIO. ESTO SE PODRIA
    // HACER DIRECTAMENTE DEL PRODUCTCONROLLER
    @Operation(summary = "Obtener promedio de valoraciones de un producto", description = "Devuelve el promedio redondeado de las valoraciones (score) de un producto específico.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Promedio calculado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado")
    })

    @GetMapping("/average/{productId}")
    public ResponseEntity<Long> getAverage(@PathVariable UUID productId) {
        Long average = ratingService.calculateAverage(productId);
        return ResponseEntity.ok(average);
    }

}