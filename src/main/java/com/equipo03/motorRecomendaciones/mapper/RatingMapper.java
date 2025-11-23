package com.equipo03.motorRecomendaciones.mapper;

import com.equipo03.motorRecomendaciones.dto.RatingRequestDTO;
import com.equipo03.motorRecomendaciones.dto.RatingResponseDTO;
import com.equipo03.motorRecomendaciones.model.Rating;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RatingMapper {

    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "product.id", target = "productId")
    RatingResponseDTO toResponseDTO(Rating rating);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "product", ignore = true)
    Rating toEntity(RatingRequestDTO dto);
}