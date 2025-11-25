package com.equipo03.motorRecomendaciones.controller;

import com.equipo03.motorRecomendaciones.dto.response.RatingResponseDTO;
import com.equipo03.motorRecomendaciones.dto.request.RatingRequestDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

public interface RatingController {

        // POST /api/ratings
        @Operation(summary = "Crear una valoración", description = "Permite a un usuario valorar un producto con un puntaje entre 1 y 5")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Valoración creada exitosamente"),
                        @ApiResponse(responseCode = "400", description = "Error en la solicitud (score inválido o usuario ya valoró)"),
                        @ApiResponse(responseCode = "404", description = "Usuario o producto no encontrado")
        })
        ResponseEntity<RatingResponseDTO> createRating(@RequestBody RatingRequestDTO request,
                        Authentication authentication);

        // GET /api/ratings/average/{productId}
        @Operation(summary = "Obtener promedio de valoraciones de un producto", description = "Devuelve el promedio redondeado de las valoraciones (score) de un producto específico.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Promedio calculado exitosamente"),
                        @ApiResponse(responseCode = "404", description = "Producto no encontrado")
        })
        ResponseEntity<Long> getAverage(@PathVariable UUID productId);
}
