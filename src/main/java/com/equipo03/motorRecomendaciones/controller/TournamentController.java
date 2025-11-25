package com.equipo03.motorRecomendaciones.controller;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import com.equipo03.motorRecomendaciones.dto.*;
import com.equipo03.motorRecomendaciones.dto.request.TournamentJoinRequestDTO;
import com.equipo03.motorRecomendaciones.dto.request.TournamentRequestDTO;
import com.equipo03.motorRecomendaciones.dto.response.TournamentCreatedResponseDTO;
import com.equipo03.motorRecomendaciones.dto.response.TournamentDetailResponseDTO;
import com.equipo03.motorRecomendaciones.dto.response.TournamentJoinResponseDTO;
import com.equipo03.motorRecomendaciones.dto.response.TournamentResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;

public interface TournamentController {

  // GET /api/tournaments
  @Operation(summary = "Obtener lista de torneos", description = "Devuelve todos los torneos con filtros opcionales: page, size, sort, status, game, q.")
  ResponseEntity<List<TournamentResponseDTO>> getTournaments(
      @Parameter(description = "Número de página") @RequestParam(required = false) Integer page,
      @Parameter(description = "Cantidad de elementos por página") @RequestParam(required = false) Integer size,
      @Parameter(description = "Campo por el cual ordenar") @RequestParam(required = false) String sort,
      @Parameter(description = "Filtrar por estado del torneo") @RequestParam(required = false) String status,
      @Parameter(description = "Filtrar por juego") @RequestParam(required = false) String game,
      @Parameter(description = "Búsqueda general por nombre") @RequestParam(required = false) String q);

  // GET /api/tournaments/{id}
  @Operation(summary = "Obtener detalles de un torneo", description = "Devuelve información completa del torneo, incluyendo participantes.")
  ResponseEntity<TournamentDetailResponseDTO> getTournamentById(
      @Parameter(description = "ID del torneo", required = true) @PathVariable Long id);

  // POST /api/tournaments
  @Operation(summary = "Crear un nuevo torneo", description = "Crea un torneo. Solo para administradores.")
  ResponseEntity<TournamentCreatedResponseDTO> createTournament(
      @Parameter(description = "Datos del torneo a crear", required = true) @Valid @RequestBody TournamentRequestDTO dto);

  // POST /api/tournaments/{id}/join
  @Operation(summary = "Unirse a un torneo", description = "Permite a un jugador inscribirse en un torneo existente.")
  ResponseEntity<TournamentJoinResponseDTO> join(
      @Parameter(description = "ID del torneo", required = true) @PathVariable Long id,
      @Parameter(description = "Opcional: contiene idUsuario nickname", required = false) @RequestBody(required = false) TournamentJoinRequestDTO request,
      Authentication authentication);

  // DELETE /api/tournaments/{id}
  @Operation(summary = "Eliminar un torneo", description = "Elimina un torneo por su ID. Solo administradores.")
  ResponseEntity<Void> delete(
      @Parameter(description = "ID del torneo a eliminar", required = true) @PathVariable Long id);
}
