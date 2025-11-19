package com.equipo03.motorRecomendaciones.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.equipo03.motorRecomendaciones.dto.*;
import com.equipo03.motorRecomendaciones.service.TournamentService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/tournaments")
public class TournamentController {

        @Autowired
        private TournamentService tournamentService;

        // GET /api/tournaments
        @Operation(summary = "Obtener lista de torneos", description = "Devuelve todos los torneos con filtros opcionales: page, size, sort, status, game, q.")
        @ApiResponse(responseCode = "200", description = "Lista de torneos obtenida correctamente")
        @GetMapping
        public ResponseEntity<List<TournamentResponseDTO>> getTournaments(
                        @Parameter(description = "Número de página") @RequestParam(required = false) Integer page,
                        @Parameter(description = "Cantidad de elementos por página") @RequestParam(required = false) Integer size,
                        @Parameter(description = "Campo por el cual ordenar") @RequestParam(required = false) String sort,
                        @Parameter(description = "Filtrar por estado del torneo") @RequestParam(required = false) String status,
                        @Parameter(description = "Filtrar por juego") @RequestParam(required = false) String game,
                        @Parameter(description = "Búsqueda general por nombre") @RequestParam(required = false) String q) {

                List<TournamentResponseDTO> tournaments = tournamentService.getTournaments(
                                page, size, sort, status, game, q);

                return ResponseEntity.ok(tournaments);
        }

        // GET /api/tournaments/{id}
        @Operation(summary = "Obtener detalles de un torneo", description = "Devuelve información completa del torneo, incluyendo participantes.")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Torneo encontrado"),
                        @ApiResponse(responseCode = "404", description = "El torneo no existe", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                                        {
                                          "status": 404,
                                          "error": "Not Found",
                                          "message": "No se encontró el torneo con ID 99",
                                          "timestamp": "2025-11-19T15:47:20.627Z",
                                          "path": "/api/tournaments/99"
                                        }
                                        """), schema = @Schema(implementation = ApiError.class)))
        })
        @GetMapping("/{id}")
        public ResponseEntity<TournamentDetailResponseDTO> getTournamentById(
                        @Parameter(description = "ID del torneo", required = true) @PathVariable Long id) {

                return ResponseEntity.ok(
                                tournamentService.findTournamentById(id));
        }

        // POST /api/tournaments
        @Operation(summary = "Crear un nuevo torneo", description = "Crea un torneo. Solo para administradores.")
        @ApiResponses({
                        @ApiResponse(responseCode = "201", description = "Torneo creado correctamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = TournamentCreatedResponseDTO.class))),
                        @ApiResponse(responseCode = "400", description = "Datos inválidos", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                                        {
                                          "status": 400,
                                          "error": "Bad Request",
                                          "message": "Datos inválidos",
                                          "messages": [
                                            "name: El nombre es obligatorio",
                                            "startDate: La fecha debe ser futura"
                                          ],
                                          "timestamp": "2025-11-19T15:47:20.624Z",
                                          "path": "/api/tournaments"
                                        }
                                        """), schema = @Schema(implementation = ApiError.class))),
                        @ApiResponse(responseCode = "500", description = "Error interno", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                                        {
                                          "status": 500,
                                          "error": "Internal Server Error",
                                          "message": "NullPointerException: tournamentService is null",
                                          "timestamp": "2025-11-19T15:47:20.631Z",
                                          "path": "/api/tournaments"
                                        }
                                        """), schema = @Schema(implementation = ApiError.class)))
        })
        @PostMapping
        public ResponseEntity<TournamentCreatedResponseDTO> createTournament(
                        @Parameter(description = "Datos del torneo a crear", required = true) @Valid @RequestBody TournamentRequestDTO dto) {

                TournamentCreatedResponseDTO created = tournamentService.createTournament(dto);
                return ResponseEntity.status(201).body(created);
        }

        // POST /api/tournaments/{id}/join
        @Operation(summary = "Unirse a un torneo", description = "Permite a un jugador inscribirse en un torneo existente.")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Inscripción completada"),
                        @ApiResponse(responseCode = "400", description = "Fuera del intervalo de inscripción o datos inválidos", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                                        {
                                          "status": 400,
                                          "error": "Bad Request",
                                          "message": "No se puede inscribir fuera del período de inscripción",
                                          "timestamp": "2025-11-19T15:47:20.624Z",
                                          "path": "/api/tournaments/1/join"
                                        }
                                        """), schema = @Schema(implementation = ApiError.class))),
                        @ApiResponse(responseCode = "404", description = "Torneo o usuario no encontrado", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                                        {
                                          "status": 404,
                                          "error": "Not Found",
                                          "message": "No se encontró el torneo con ID 1",
                                          "timestamp": "2025-11-19T15:47:20.627Z",
                                          "path": "/api/tournaments/1/join"
                                        }
                                        """), schema = @Schema(implementation = ApiError.class))),
                        @ApiResponse(responseCode = "500", description = "Error interno", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                                        {
                                          "status": 500,
                                          "error": "Internal Server Error",
                                          "message": "NullPointerException: tournamentService is null",
                                          "timestamp": "2025-11-19T15:47:20.631Z",
                                          "path": "/api/tournaments"
                                        }
                                        """), schema = @Schema(implementation = ApiError.class)))
        })
        @PostMapping("/{id}/join")
        public ResponseEntity<TournamentJoinResponseDTO> join(
                        @Parameter(description = "ID del torneo", required = true) @PathVariable Long id,
                        @Parameter(description = "Opcional: contiene idUsuario nickname", required = false) @RequestBody(required = false) TournamentJoinRequestDTO request) {

                if (request == null)
                        request = new TournamentJoinRequestDTO();

                TournamentJoinResponseDTO response = tournamentService.joinTournament(id, request);
                return ResponseEntity.ok(response);
        }

        // DELETE /api/tournaments/{id}
        @Operation(summary = "Eliminar un torneo", description = "Elimina un torneo por su ID. Solo administradores.")
        @ApiResponses({
                        @ApiResponse(responseCode = "204", description = "Torneo eliminado"),
                        @ApiResponse(responseCode = "404", description = "Torneo no encontrado", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                                        {
                                          "status": 404,
                                          "error": "Not Found",
                                          "message": "No se encontró el torneo con ID 1",
                                          "timestamp": "2025-11-19T15:47:20.627Z",
                                          "path": "/api/tournaments/1"
                                        }
                                        """), schema = @Schema(implementation = ApiError.class))),
                        @ApiResponse(responseCode = "500", description = "Error interno", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                                        {
                                          "status": 500,
                                          "error": "Internal Server Error",
                                          "message": "NullPointerException: tournamentService is null",
                                          "timestamp": "2025-11-19T15:47:20.631Z",
                                          "path": "/api/tournaments"
                                        }
                                        """), schema = @Schema(implementation = ApiError.class)))
        })
        @DeleteMapping("/{id}")
        public ResponseEntity<Void> delete(
                        @Parameter(description = "ID del torneo a eliminar", required = true) @PathVariable Long id) {

                tournamentService.deleteTournament(id);
                return ResponseEntity.noContent().build();
        }
}