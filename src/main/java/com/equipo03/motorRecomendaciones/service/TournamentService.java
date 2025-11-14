package com.equipo03.motorRecomendaciones.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.equipo03.motorRecomendaciones.dto.TournamentRequestDTO;
import com.equipo03.motorRecomendaciones.dto.TournamentResponseDTO;
import com.equipo03.motorRecomendaciones.dto.TournamentCreatedResponseDTO;
import com.equipo03.motorRecomendaciones.dto.TournamentDetailResponseDTO;
import com.equipo03.motorRecomendaciones.enums.TournamentStatus;
import com.equipo03.motorRecomendaciones.mapper.TournamentMapper;
import com.equipo03.motorRecomendaciones.model.Tournament;
import com.equipo03.motorRecomendaciones.respository.TournamentRepository;

@Service
public class TournamentService {

    @Autowired
    private TournamentRepository tournamentRepository;

    @Autowired
    private TournamentMapper tournamentMapper;

    // LISTA TODOS LOS TORNEOS (FALTA QUE DEVUELVA EL NUMERO DE PARTICIPANTES
    // INSCRIPTOS)
    public List<TournamentResponseDTO> getTournaments(Integer page, Integer size, String sort,
            String status, String game, String q) {

        int pageNumber = (page != null && page > 0) ? page : 0;
        int sizeNumber = (size != null && size > 0) ? size : 20;

        Sort sortConfig = Sort.unsorted();
        if (sort != null && !sort.isBlank()) {
            String[] sortParams = sort.split(",");
            if (sortParams.length == 2) {
                sortConfig = Sort.by(Sort.Direction.fromString(sortParams[1]), sortParams[0]);
            } else {
                sortConfig = Sort.by(sortParams[0]);
            }
        }

        sortConfig = Sort.by(Sort.Direction.ASC, "startDate");

        PageRequest pageable = PageRequest.of(pageNumber, sizeNumber, sortConfig);

        boolean hasGame = (game != null && !game.isBlank());
        boolean hasQ = (q != null && !q.isBlank());
        boolean hasStatus = (status != null && !status.isBlank());

        TournamentStatus tournamentStatus = null;
        if (hasStatus) {
            try {
                tournamentStatus = TournamentStatus.valueOf(status.trim().toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException(
                        "El estado '" + status + "' no es válido. Valores permitidos: UPCOMING, OPEN, CLOSED");
            }
        }
        Page<Tournament> tournaments;

        if (hasGame && hasQ && tournamentStatus != null) {
            tournaments = tournamentRepository.findByStatusAndGameContainingIgnoreCaseAndNameContainingIgnoreCase(
                    tournamentStatus, game, q,
                    pageable);
        } else if (hasGame && hasQ) {
            tournaments = tournamentRepository.findByGameContainingIgnoreCaseAndNameContainingIgnoreCase(game, q,
                    pageable);
        } else if (hasGame && tournamentStatus != null) {
            tournaments = tournamentRepository.findByStatusAndGameContainingIgnoreCase(tournamentStatus, game,
                    pageable);
        } else if (hasQ && tournamentStatus != null) {
            tournaments = tournamentRepository.findByStatusAndNameContainingIgnoreCase(tournamentStatus, q,
                    pageable);
        } else if (hasGame) {
            tournaments = tournamentRepository.findByGameContainingIgnoreCase(game, pageable);
        } else if (hasQ) {
            tournaments = tournamentRepository.findByNameContainingIgnoreCase(q, pageable);
        } else if (tournamentStatus != null) {
            tournaments = tournamentRepository.findByStatus(tournamentStatus, pageable);
        } else {
            tournaments = tournamentRepository.findAll(pageable);
        }
        return tournaments.stream()
                .map(tournamentMapper::toDto)
                .toList();
    }

    // DEVUELVE UN SOLO TORNEO CON SUS DATOS ( FALTA LA CANTIDAD DE
    // PARTICIPANTES INSCRIPTOS)
    public TournamentDetailResponseDTO findTournamentById(Long id) {
        Optional<Tournament> optionalTournament = tournamentRepository.findById(id);
        if (optionalTournament.isEmpty()) {
            // throw new Error("El torneo no existe");
            return null;
        }
        return tournamentMapper.toDetailDto(optionalTournament.get());
    }

    // CREA UN TORNEO (SOLO ADMIN)
    public TournamentCreatedResponseDTO createTournament(TournamentRequestDTO tournament) {
        Tournament tournamentRequest = tournamentMapper.toEntity(tournament);

        if (tournamentRepository.findByName(tournamentRequest.getName()).isPresent()) {
            throw new IllegalArgumentException("Ya existe un torneo con ese nombre");
        }
        if (!this.validateDate(tournamentRequest.getStartDate(), tournamentRequest.getEndDate())) {
            throw new IllegalArgumentException("fechas del torneo inválidas");
        }
        if (!this.validateDate(tournamentRequest.getRegistrationOpenAt(), tournamentRequest.getRegistrationCloseAt())) {
            throw new IllegalArgumentException("fechas de inscripción al torneo inválidas");
        }
        if (tournamentRequest.getRegistrationOpenAt().isAfter(tournamentRequest.getStartDate())) {
            throw new IllegalArgumentException("El cierre de inscripción no puede ser después del inicio del torneo");
        }
        tournamentRequest.setCreatedAt(LocalDateTime.now());
        Tournament savedTournament = tournamentRepository.save(tournamentRequest);
        return tournamentMapper.toCreatedDto(savedTournament);
    }

    // FALTA LA INSCRIPCION DE UN USUARIO A UN TORNEO

    // VALIDAR FECHAS
    public boolean validateDate(LocalDateTime dateInit, LocalDateTime dateEnd) {
        LocalDateTime now = LocalDateTime.now();

        if (dateInit.isAfter(dateEnd))
            return false;

        if (dateInit.isBefore(now))
            return false;

        if (dateEnd.isBefore(now))
            return false;
        return true;
    }
}
