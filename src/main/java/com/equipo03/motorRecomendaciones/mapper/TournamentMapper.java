package com.equipo03.motorRecomendaciones.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.equipo03.motorRecomendaciones.dto.response.TournamentCreatedResponseDTO;
import com.equipo03.motorRecomendaciones.dto.response.TournamentDetailResponseDTO;
import com.equipo03.motorRecomendaciones.dto.request.TournamentRequestDTO;
import com.equipo03.motorRecomendaciones.dto.response.TournamentResponseDTO;
import com.equipo03.motorRecomendaciones.model.Tournament;
import com.equipo03.motorRecomendaciones.model.TournamentParticipation;

@Mapper(config = MapperConfiguration.class)
public interface TournamentMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "participations", ignore = true)
    Tournament toEntity(TournamentRequestDTO dto);

    TournamentResponseDTO toDto(Tournament entity);

    TournamentCreatedResponseDTO toCreatedDto(Tournament entity);

    @Mapping(target = "participants", source = "participations")
    TournamentDetailResponseDTO toDetailDto(Tournament entity);

    default List<String> mapParticipants(List<TournamentParticipation> participations) {
        if (participations == null)
            return java.util.Collections.emptyList();

        return participations.stream()
                .map(p -> p.getUser().getUsername())
                .toList();
    }
}
