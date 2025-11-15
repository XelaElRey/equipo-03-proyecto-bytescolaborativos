package com.equipo03.motorRecomendaciones.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.equipo03.motorRecomendaciones.dto.TournamentCreatedResponseDTO;
import com.equipo03.motorRecomendaciones.dto.TournamentDetailResponseDTO;
import com.equipo03.motorRecomendaciones.dto.TournamentJoinRequestDTO;
import com.equipo03.motorRecomendaciones.dto.TournamentJoinResponseDTO;
import com.equipo03.motorRecomendaciones.dto.TournamentRequestDTO;
import com.equipo03.motorRecomendaciones.dto.TournamentResponseDTO;
import com.equipo03.motorRecomendaciones.service.TournamentService;

@RestController
@RequestMapping("/api/tournaments")
public class TournamentController {

    @Autowired
    private TournamentService tournamentService;

    @GetMapping
    public ResponseEntity<List<TournamentResponseDTO>> getTournaments(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) String sort,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String game,
            @RequestParam(required = false) String q) {

        List<TournamentResponseDTO> tournaments = tournamentService.getTournaments(
                page, size, sort, status, game, q);

        return ResponseEntity.ok(tournaments);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getTournamentById(@PathVariable Long id) {
        TournamentDetailResponseDTO dto = tournamentService.findTournamentById(id);
        if (dto == null) {
            return ResponseEntity.status(404).body("El torneo no existe");
        }
        return ResponseEntity.ok(dto);
    }

    // Hay que revisar que solo pueda hacerlo un admin
    @PostMapping
    public ResponseEntity<TournamentCreatedResponseDTO> createTournament(
            @RequestBody TournamentRequestDTO dto) {

        TournamentCreatedResponseDTO created = tournamentService.createTournament(dto);
        return ResponseEntity.status(201).body(created);
    }

    // Hay que revisar que se autentique el usuario, por el momento mandare su id en
    // el body
    @PostMapping("/{id}/join")
    public ResponseEntity<TournamentJoinResponseDTO> join(
            @PathVariable Long id,
            @RequestBody(required = false) TournamentJoinRequestDTO request) {

        if (request == null)
            request = new TournamentJoinRequestDTO();

        TournamentJoinResponseDTO response = tournamentService.joinTournament(id, request);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        tournamentService.deleteTournament(id);
        return ResponseEntity.noContent().build();
    }

}
