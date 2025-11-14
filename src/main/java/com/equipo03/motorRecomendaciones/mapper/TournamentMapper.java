package com.equipo03.motorRecomendaciones.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.equipo03.motorRecomendaciones.dto.TournamentCreatedResponseDTO;
import com.equipo03.motorRecomendaciones.dto.TournamentDetailResponseDTO;
import com.equipo03.motorRecomendaciones.dto.TournamentRequestDTO;
import com.equipo03.motorRecomendaciones.dto.TournamentResponseDTO;
import com.equipo03.motorRecomendaciones.model.Tournament;

@Mapper(componentModel = "spring")
public interface TournamentMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    Tournament toEntity(TournamentRequestDTO dto);

    TournamentResponseDTO toDto(Tournament entity);

    TournamentCreatedResponseDTO toCreatedDto(Tournament entity);

    TournamentDetailResponseDTO toDetailDto(Tournament entity);
}
