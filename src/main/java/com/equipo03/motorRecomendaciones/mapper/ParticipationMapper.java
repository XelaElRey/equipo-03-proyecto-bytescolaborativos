package com.equipo03.motorRecomendaciones.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.equipo03.motorRecomendaciones.dto.TournamentJoinRequestDTO;
import com.equipo03.motorRecomendaciones.dto.TournamentJoinResponseDTO;
import com.equipo03.motorRecomendaciones.model.TournamentParticipation;

@Mapper(config = MapperConfiguration.class)
public interface ParticipationMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "tournament", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "registeredAt", ignore = true)
    TournamentParticipation toEntity(TournamentJoinRequestDTO dto);

    @Mapping(target = "message", constant = "Inscripción completada")
    @Mapping(target = "status", constant = "REGISTERED")
    @Mapping(source = "participation.tournament.id", target = "tournamentId")
    @Mapping(source = "participation.user.id", target = "userId")
    TournamentJoinResponseDTO toResponseDTO(TournamentParticipation participation);
}
