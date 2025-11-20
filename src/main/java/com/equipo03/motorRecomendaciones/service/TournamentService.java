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
import com.equipo03.motorRecomendaciones.dto.TournamentRequestDTO;
import com.equipo03.motorRecomendaciones.dto.TournamentResponseDTO;
import com.equipo03.motorRecomendaciones.exception.BadRequestException;
import com.equipo03.motorRecomendaciones.exception.ResourceNotFoundException;
import com.equipo03.motorRecomendaciones.dto.TournamentCreatedResponseDTO;
import com.equipo03.motorRecomendaciones.dto.TournamentDetailResponseDTO;
import com.equipo03.motorRecomendaciones.dto.TournamentJoinRequestDTO;
import com.equipo03.motorRecomendaciones.dto.TournamentJoinResponseDTO;
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
        if (!this.validateDate(tournamentRequest.getRegistrationOpenAt(), tournamentRequest.getRegistrationCloseAt())) {
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
    // EN EL REQUEST VIENE EL ID, PERO EN REALIDAD PRIMERO SE COMPRUEBA QUE PUEDE
    // ACCEDER A ESTA PARTE CON EL TOKEN
    // CON LO QUE SI LLEGA HASTA AQUI ES PORQUE SI EXISTE Y PUEDE
    public TournamentJoinResponseDTO joinTournament(Long idTorneo, TournamentJoinRequestDTO request) {
        Optional<Tournament> tournamentOptional = tournamentRepository.findById(idTorneo);
        if (!tournamentOptional.isPresent()) {
            throw new ResourceNotFoundException("Torneo no encontrado");
        }
        // En el request trae el userId pero en realidad deberia hacerse por
        // autenticacion. CORREGIR
        Optional<User> userOptional = userRepository.findById(request.getUserId());
        if (!userOptional.isPresent()) {
            throw new ResourceNotFoundException("Usuario no encontrado");
        }

        Tournament tournament = tournamentOptional.get();
        User user = userOptional.get();

        // validacion de que se esta inscribiendo dentro del rango permitido de fechas
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(tournament.getRegistrationOpenAt()) || now.isAfter(tournament.getRegistrationCloseAt())) {
            throw new BadRequestException("Estas intenando inscribirte fuera del rango de fechas de inscripción");
        }

        // validar que hay plazas disponibles
        Integer currentParticipants = participationRepository.countByTournamentId(idTorneo);
        Integer maxParticipants = tournament.getMaxParticipants();

        if (maxParticipants != null && currentParticipants >= maxParticipants) {
            throw new BadRequestException("No es posible inscribirse, no hay plazas disponibles");
        }

        // Asegurarse que el usuario no esté ya inscrito
        if (participationRepository.existsByTournamentIdAndUserId(tournament.getId(), request.getUserId())) {
            throw new BadRequestException("Ya estás inscrito en este torneo");
        }

        TournamentParticipation participation = participationMapper.toEntity(request);
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
