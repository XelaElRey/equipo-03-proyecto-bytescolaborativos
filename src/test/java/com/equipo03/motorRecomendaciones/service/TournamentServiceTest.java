package com.equipo03.motorRecomendaciones.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.*;
import java.time.LocalDateTime;
import java.util.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import com.equipo03.motorRecomendaciones.dto.response.*;
import com.equipo03.motorRecomendaciones.dto.request.*;
import com.equipo03.motorRecomendaciones.model.enums.TournamentStatus;
import com.equipo03.motorRecomendaciones.exception.*;
import com.equipo03.motorRecomendaciones.mapper.*;
import com.equipo03.motorRecomendaciones.model.*;
import com.equipo03.motorRecomendaciones.repository.*;
import com.equipo03.motorRecomendaciones.service.TournamentService;

@ExtendWith(MockitoExtension.class)
class TournamentServiceTest {

    @Mock
    private TournamentRepository tournamentRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TournamentParticipationRepository participationRepository;

    @Mock
    private TournamentMapper tournamentMapper;

    @Mock
    private ParticipationMapper participationMapper;

    @InjectMocks
    private TournamentService tournamentService;

    private Tournament sampleTournament;
    private TournamentResponseDTO sampleTournamentDTO;

    private LocalDateTime now;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private LocalDateTime regOpen;
    private LocalDateTime regClose;

    private Pageable defaultPageable;

    @BeforeEach
    void setUp() {

        // ----- Fechas comunes -----
        now = LocalDateTime.now();
        startDate = now.plusDays(5);
        endDate = now.plusDays(7);
        regOpen = now.minusDays(1);
        regClose = now.plusDays(2);

        // ----- Tournament de ejemplo -----
        sampleTournament = Tournament.builder()
                .id(1L)
                .name("Torneo Test")
                .game("Valorant")
                .rules("Reglas básicas")
                .startDate(startDate)
                .endDate(endDate)
                .registrationOpenAt(regOpen)
                .registrationCloseAt(regClose)
                .createdAt(now)
                .maxParticipants(32)
                .status(TournamentStatus.UPCOMING)
                .build();

        // ----- DTO esperado -----
        sampleTournamentDTO = new TournamentResponseDTO(
                1L,
                "Torneo Test",
                "Valorant",
                TournamentStatus.UPCOMING.name(),
                0);

        // ----- Pageable real usado por el servicio -----
        defaultPageable = PageRequest.of(
                0,
                20,
                Sort.by(Sort.Direction.ASC, "startDate"));
    }

    // ============================================================
    // TESTS DE getTournaments()
    // ============================================================

    @Test
    void getTournaments_devuelveListaDeDtos_cuandoNoHayFiltros() {

        Page<Tournament> pageMock = new PageImpl<>(List.of(sampleTournament));

        when(tournamentRepository.findAll(defaultPageable))
                .thenReturn(pageMock);

        when(tournamentMapper.toDto(sampleTournament))
                .thenReturn(sampleTournamentDTO);

        when(participationRepository.countByTournamentId(1L))
                .thenReturn(3);

        List<TournamentResponseDTO> result = tournamentService.getTournaments(null, null, null, null, null, null);

        assertEquals(1, result.size());
        assertEquals("Torneo Test", result.get(0).getName());
        assertEquals(3, result.get(0).getParticipants());

        verify(tournamentRepository).findAll(defaultPageable);
        verify(tournamentMapper).toDto(sampleTournament);
    }

    @Test
    void getTournaments_filtraPorStatus_cuandoStatusEsValido() {

        Page<Tournament> pageMock = new PageImpl<>(List.of(sampleTournament));

        when(tournamentRepository.findByStatus(eq(TournamentStatus.OPEN), any(PageRequest.class)))
                .thenReturn(pageMock);

        when(tournamentMapper.toDto(sampleTournament))
                .thenReturn(sampleTournamentDTO);

        List<TournamentResponseDTO> result = tournamentService.getTournaments(null, null, null, "OPEN", null, null);

        assertEquals(1, result.size());
        assertEquals("Torneo Test", result.get(0).getName());
    }

    @Test
    void getTournaments_lanzaExcepcion_cuandoStatusEsInvalido() {

        assertThrows(BadRequestException.class,
                () -> tournamentService.getTournaments(null, null, null, "NOT_A_STATUS", null, null));
    }

    // ============================================================
    // TESTS DE findTournamentById()
    // ============================================================

    @Test
    void findTournamentById_devuelveDto_cuandoExiste() {

        when(tournamentRepository.findById(1L))
                .thenReturn(Optional.of(sampleTournament));

        TournamentDetailResponseDTO detailDTO = mock(TournamentDetailResponseDTO.class);

        when(tournamentMapper.toDetailDto(sampleTournament))
                .thenReturn(detailDTO);

        TournamentDetailResponseDTO result = tournamentService.findTournamentById(1L);

        assertNotNull(result);
        verify(tournamentRepository).findById(1L);
        verify(tournamentMapper).toDetailDto(sampleTournament);
    }

    @Test
    void findTournamentById_lanzaExcepcion_cuandoNoExiste() {

        when(tournamentRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> tournamentService.findTournamentById(1L));
    }

    @Test
    void createTournament_creaCorrectamente_cuandoDatosValidos() {
        // Mock del DTO de solicitud
        TournamentRequestDTO requestDTO = mock(TournamentRequestDTO.class);

        // Construimos manualmente el Tournament usando builder
        Tournament tournamentEntity = Tournament.builder()
                .id(sampleTournament.getId())
                .name(sampleTournament.getName())
                .game(sampleTournament.getGame())
                .rules(sampleTournament.getRules())
                .startDate(sampleTournament.getStartDate())
                .endDate(sampleTournament.getEndDate())
                .registrationOpenAt(sampleTournament.getRegistrationOpenAt())
                .registrationCloseAt(sampleTournament.getRegistrationCloseAt())
                .createdAt(sampleTournament.getCreatedAt())
                .maxParticipants(sampleTournament.getMaxParticipants())
                .status(sampleTournament.getStatus())
                .participations(sampleTournament.getParticipations())
                .build();

        // Cuando el mapper convierte el DTO de request a la entidad, devuelve nuestra
        // entidad mock
        when(tournamentMapper.toEntity(requestDTO)).thenReturn(tournamentEntity);

        // Simula que no existe ningún torneo con el mismo nombre
        when(tournamentRepository.findByName(tournamentEntity.getName()))
                .thenReturn(Optional.empty());

        // Simula que guardar la entidad en el repositorio devuelve la misma entidad
        when(tournamentRepository.save(tournamentEntity))
                .thenReturn(tournamentEntity);

        // Mock del DTO que devuelve el mapper al crear el torneo
        TournamentCreatedResponseDTO createdDTO = mock(TournamentCreatedResponseDTO.class);
        when(tournamentMapper.toCreatedDto(tournamentEntity))
                .thenReturn(createdDTO);

        // Llamada al método que estamos probando
        TournamentCreatedResponseDTO result = tournamentService.createTournament(requestDTO);

        // Verificaciones
        assertNotNull(result); // El resultado no debe ser null
        verify(tournamentRepository).save(tournamentEntity); // Se debe haber llamado a save con la entidad
        verify(tournamentMapper).toCreatedDto(tournamentEntity); // Se debe haber llamado al mapper para el DTO
    }

    @Test
    void createTournament_lanzaExcepcion_cuandoNombreYaExiste() {

        TournamentRequestDTO requestDTO = mock(TournamentRequestDTO.class);

        Tournament tournamentEntity = Tournament.builder()
                .id(sampleTournament.getId())
                .name(sampleTournament.getName())
                .game(sampleTournament.getGame())
                .rules(sampleTournament.getRules())
                .startDate(sampleTournament.getStartDate())
                .endDate(sampleTournament.getEndDate())
                .registrationOpenAt(sampleTournament.getRegistrationOpenAt())
                .registrationCloseAt(sampleTournament.getRegistrationCloseAt())
                .createdAt(sampleTournament.getCreatedAt())
                .maxParticipants(sampleTournament.getMaxParticipants())
                .status(sampleTournament.getStatus())
                .participations(sampleTournament.getParticipations())
                .build();

        when(tournamentMapper.toEntity(requestDTO))
                .thenReturn(tournamentEntity);

        when(tournamentRepository.findByName(tournamentEntity.getName()))
                .thenReturn(Optional.of(tournamentEntity));

        assertThrows(BadRequestException.class,
                () -> tournamentService.createTournament(requestDTO));
    }

    @Test
    void createTournament_lanzaExcepcion_cuandoFechasTorneoInvalidas() {

        TournamentRequestDTO requestDTO = mock(TournamentRequestDTO.class);

        Tournament tournamentEntity = Tournament.builder()
                .id(sampleTournament.getId())
                .name(sampleTournament.getName())
                .game(sampleTournament.getGame())
                .rules(sampleTournament.getRules())
                .startDate(now.minusDays(1)) // inválido
                .endDate(now.minusDays(2)) // inválido
                .registrationOpenAt(sampleTournament.getRegistrationOpenAt())
                .registrationCloseAt(sampleTournament.getRegistrationCloseAt())
                .createdAt(sampleTournament.getCreatedAt())
                .maxParticipants(sampleTournament.getMaxParticipants())
                .status(sampleTournament.getStatus())
                .participations(sampleTournament.getParticipations())
                .build();

        when(tournamentMapper.toEntity(requestDTO))
                .thenReturn(tournamentEntity);

        when(tournamentRepository.findByName(tournamentEntity.getName()))
                .thenReturn(Optional.empty());

        assertThrows(BadRequestException.class,
                () -> tournamentService.createTournament(requestDTO));
    }

    @Test
    void createTournament_lanzaExcepcion_cuandoRegistroEmpiezaDespuesDelTorneo() {

        TournamentRequestDTO requestDTO = mock(TournamentRequestDTO.class);

        Tournament tournamentEntity = Tournament.builder()
                .id(sampleTournament.getId())
                .name(sampleTournament.getName())
                .game(sampleTournament.getGame())
                .rules(sampleTournament.getRules())
                .startDate(sampleTournament.getStartDate())
                .endDate(sampleTournament.getEndDate())
                .registrationOpenAt(sampleTournament.getStartDate().plusDays(1)) // después del inicio
                .registrationCloseAt(sampleTournament.getRegistrationCloseAt())
                .createdAt(sampleTournament.getCreatedAt())
                .maxParticipants(sampleTournament.getMaxParticipants())
                .status(sampleTournament.getStatus())
                .participations(sampleTournament.getParticipations())
                .build();

        when(tournamentMapper.toEntity(requestDTO))
                .thenReturn(tournamentEntity);

        when(tournamentRepository.findByName(tournamentEntity.getName()))
                .thenReturn(Optional.empty());

        assertThrows(BadRequestException.class,
                () -> tournamentService.createTournament(requestDTO));
    }

    @Test
    void joinTournament_inscripcionExitosa() {
        UUID userId = UUID.randomUUID();

        User user = User.builder()
                .id(userId)
                .username("player")
                .build();

        when(tournamentRepository.findById(1L))
                .thenReturn(Optional.of(sampleTournament));

        when(userRepository.findByUsername("player"))
                .thenReturn(Optional.of(user));

        when(participationRepository.countByTournamentId(1L))
                .thenReturn(10);

        when(participationRepository.existsByTournamentIdAndUserId(1L, userId))
                .thenReturn(false);

        TournamentParticipation participation = new TournamentParticipation();
        participation.setTournament(sampleTournament);
        participation.setUser(user);

        when(participationRepository.save(any()))
                .thenReturn(participation);

        TournamentJoinResponseDTO responseDTO = mock(TournamentJoinResponseDTO.class);

        when(participationMapper.toResponseDTO(participation))
                .thenReturn(responseDTO);

        TournamentJoinResponseDTO result = tournamentService.joinTournament(1L, "player",
                new TournamentJoinRequestDTO());

        assertNotNull(result);
        verify(participationRepository).save(any());
    }

    @Test
    void joinTournament_lanzaExcepcion_cuandoTorneoNoExiste() {

        when(tournamentRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> tournamentService.joinTournament(1L, "player", new TournamentJoinRequestDTO()));
    }

    @Test
    void joinTournament_lanzaExcepcion_cuandoUsuarioNoExiste() {

        when(tournamentRepository.findById(1L))
                .thenReturn(Optional.of(sampleTournament));

        when(userRepository.findByUsername("player"))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> tournamentService.joinTournament(1L, "player", new TournamentJoinRequestDTO()));
    }

    @Test
    void joinTournament_lanzaExcepcion_fueraDeRangoInscripcion() {

        Tournament tournament = Tournament.builder()
                .id(sampleTournament.getId())
                .name(sampleTournament.getName())
                .game(sampleTournament.getGame())
                .rules(sampleTournament.getRules())
                .startDate(sampleTournament.getStartDate())
                .endDate(sampleTournament.getEndDate())
                .registrationOpenAt(now.plusDays(3)) // abrimos en el futuro para provocar la excepción
                .registrationCloseAt(sampleTournament.getRegistrationCloseAt())
                .createdAt(sampleTournament.getCreatedAt())
                .maxParticipants(sampleTournament.getMaxParticipants())
                .status(sampleTournament.getStatus())
                .participations(sampleTournament.getParticipations())
                .build();

        when(tournamentRepository.findById(1L))
                .thenReturn(Optional.of(tournament));

        when(userRepository.findByUsername(anyString()))
                .thenReturn(Optional.of(new User()));

        assertThrows(BadRequestException.class,
                () -> tournamentService.joinTournament(1L, "user", new TournamentJoinRequestDTO()));
    }

    @Test
    void joinTournament_lanzaExcepcion_cuandoTorneoLleno() {

        when(tournamentRepository.findById(1L)).thenReturn(Optional.of(sampleTournament));
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(new User()));

        when(participationRepository.countByTournamentId(1L))
                .thenReturn(32); // maxParticipants = 32 → lleno

        assertThrows(BadRequestException.class,
                () -> tournamentService.joinTournament(1L, "user", new TournamentJoinRequestDTO()));
    }

    @Test
    void joinTournament_lanzaExcepcion_cuandoUsuarioYaInscrito() {

        // Creamos un UUID para el usuario
        UUID userId = UUID.randomUUID();

        // Usuario de prueba con ID UUID
        User user = User.builder()
                .id(userId)
                .build();

        // Mocks de repositorios
        when(tournamentRepository.findById(1L))
                .thenReturn(Optional.of(sampleTournament));

        when(userRepository.findByUsername("player"))
                .thenReturn(Optional.of(user));

        when(participationRepository.countByTournamentId(1L))
                .thenReturn(10);

        // Simulamos que el usuario ya está inscrito
        when(participationRepository.existsByTournamentIdAndUserId(1L, userId))
                .thenReturn(true);

        // Verificamos que se lance la excepción
        assertThrows(BadRequestException.class,
                () -> tournamentService.joinTournament(
                        1L,
                        "player",
                        new TournamentJoinRequestDTO()));
    }

    @Test
    void deleteTournament_eliminaCorrectamente() {

        when(tournamentRepository.existsById(1L)).thenReturn(true);
        when(participationRepository.countByTournamentId(1L)).thenReturn(0);

        tournamentService.deleteTournament(1L);

        verify(tournamentRepository).deleteById(1L);
    }

    @Test
    void deleteTournament_lanzaExcepcion_cuandoNoExiste() {

        when(tournamentRepository.existsById(1L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class,
                () -> tournamentService.deleteTournament(1L));
    }

    @Test
    void deleteTournament_lanzaExcepcion_cuandoTieneParticipantes() {

        when(tournamentRepository.existsById(1L)).thenReturn(true);
        when(participationRepository.countByTournamentId(1L)).thenReturn(5);

        assertThrows(BadRequestException.class,
                () -> tournamentService.deleteTournament(1L));
    }

    @Test
    void validateDate_devuelveTrue_cuandoFechasValidas() {
        boolean result = tournamentService.validateDate(now.plusDays(1), now.plusDays(3));
        assertTrue(result);
    }

    @Test
    void validateDate_devuelveFalse_cuandoInitDespuesDeEnd() {
        boolean result = tournamentService.validateDate(now.plusDays(5), now.plusDays(2));
        assertFalse(result);
    }

    @Test
    void validateDate_devuelveFalse_cuandoInicioEsPasado() {
        boolean result = tournamentService.validateDate(now.minusDays(1), now.plusDays(3));
        assertFalse(result);
    }

    @Test
    void validateDate_devuelveFalse_cuandoFinEsPasado() {
        boolean result = tournamentService.validateDate(now.plusDays(1), now.minusDays(1));
        assertFalse(result);
    }

}
