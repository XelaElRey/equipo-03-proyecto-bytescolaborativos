package com.equipo03.motorRecomendaciones.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.equipo03.motorRecomendaciones.dto.request.TournamentRequestDTO;
import com.equipo03.motorRecomendaciones.dto.response.TournamentResponseDTO;
import com.equipo03.motorRecomendaciones.exception.BadRequestException;
import com.equipo03.motorRecomendaciones.exception.ResourceNotFoundException;
import com.equipo03.motorRecomendaciones.dto.response.TournamentCreatedResponseDTO;
import com.equipo03.motorRecomendaciones.dto.response.TournamentDetailResponseDTO;
import com.equipo03.motorRecomendaciones.dto.request.TournamentJoinRequestDTO;
import com.equipo03.motorRecomendaciones.dto.response.TournamentJoinResponseDTO;
import com.equipo03.motorRecomendaciones.mapper.ParticipationMapper;
import com.equipo03.motorRecomendaciones.mapper.TournamentMapper;
import com.equipo03.motorRecomendaciones.model.Tournament;
import com.equipo03.motorRecomendaciones.model.TournamentParticipation;
import com.equipo03.motorRecomendaciones.model.User;
import com.equipo03.motorRecomendaciones.model.enums.TournamentStatus;
import com.equipo03.motorRecomendaciones.repository.TournamentParticipationRepository;
import com.equipo03.motorRecomendaciones.repository.TournamentRepository;
import com.equipo03.motorRecomendaciones.repository.UserRepository;

@Service
public class TournamentService {

    @Autowired
    private TournamentRepository tournamentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TournamentParticipationRepository participationRepository;

    @Autowired
    private TournamentMapper tournamentMapper;

    @Autowired
    private ParticipationMapper participationMapper;

    // LISTA TODOS LOS TORNEOS
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
                throw new BadRequestException(
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
                .map(t -> {
                    t.setParticipants(participationRepository.countByTournamentId(t.getId()));
                    return t;
                })
                .toList();
    }

    // DEVUELVE UN SOLO TORNEO CON SUS DATOS
    public TournamentDetailResponseDTO findTournamentById(Long id) {
        Optional<Tournament> optionalTournament = tournamentRepository.findById(id);
        if (optionalTournament.isEmpty()) {
            throw new ResourceNotFoundException("El torneo no existe");
        }

        return tournamentMapper.toDetailDto(optionalTournament.get());
    }

    // CREA UN TORNEO (FALTA CONTROLAR QUE SEA SOLO ADMIN)
    public TournamentCreatedResponseDTO createTournament(TournamentRequestDTO tournament) {
        Tournament tournamentRequest = tournamentMapper.toEntity(tournament);

        if (tournamentRepository.findByName(tournamentRequest.getName()).isPresent()) {
            throw new BadRequestException("Ya existe un torneo con ese nombre");
        }
        if (!this.validateDate(tournamentRequest.getStartDate(), tournamentRequest.getEndDate())) {
            throw new BadRequestException("fechas del torneo inválidas");
        }
        if (tournamentRequest.getRegistrationOpenAt().isAfter(tournamentRequest.getRegistrationCloseAt())||tournamentRequest.getRegistrationCloseAt().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("fechas de inscripción al torneo inválidas");
        }
        if (tournamentRequest.getRegistrationOpenAt().isAfter(tournamentRequest.getStartDate())) {
            throw new BadRequestException("El cierre de inscripción no puede ser después del inicio del torneo");
        }
        tournamentRequest.setCreatedAt(LocalDateTime.now());
        Tournament savedTournament = tournamentRepository.save(tournamentRequest);
        return tournamentMapper.toCreatedDto(savedTournament);
    }

    // PERMITE A UN USUARIO UNIRSE A UN TORNEO
    @Transactional
    public TournamentJoinResponseDTO joinTournament(Long idTorneo, String username, TournamentJoinRequestDTO request) {
        Tournament tournament = tournamentRepository.findById(idTorneo)
                .orElseThrow(() -> new ResourceNotFoundException("Torneo no encontrado"));

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(tournament.getRegistrationOpenAt()) || now.isAfter(tournament.getRegistrationCloseAt())) {
            throw new BadRequestException("Estas intentando inscribirte fuera del rango de fechas de inscripción");
        }

        Integer currentParticipants = participationRepository.countByTournamentId(idTorneo);
        Integer maxParticipants = tournament.getMaxParticipants();

        if (maxParticipants != null && currentParticipants >= maxParticipants) {
            throw new BadRequestException("No es posible inscribirse, no hay plazas disponibles");
        }

        if (participationRepository.existsByTournamentIdAndUserId(tournament.getId(), user.getId())) {
            throw new BadRequestException("Ya estás inscrito en este torneo");
        }

        TournamentParticipation participation = new TournamentParticipation();
        participation.setTournament(tournament);
        participation.setUser(user);
        participation.setRegisteredAt(LocalDateTime.now());

        TournamentParticipation saved = participationRepository.save(participation);

        return participationMapper.toResponseDTO(saved);
    }

    // ELIMINAR TORNEO POR ID
    public void deleteTournament(Long id) {

        if (!tournamentRepository.existsById(id)) {
            throw new ResourceNotFoundException("El torneo con ID " + id + " no existe");
        }
        if (participationRepository.countByTournamentId(id) > 0) {
            throw new BadRequestException("No es posible borrar este torneo, tiene participantes inscritos");
        }
        tournamentRepository.deleteById(id);
    }

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
