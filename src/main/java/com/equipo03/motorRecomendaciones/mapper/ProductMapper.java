package com.equipo03.motorRecomendaciones.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.equipo03.motorRecomendaciones.dto.ProductDTO;
import com.equipo03.motorRecomendaciones.model.Product;

@Mapper(config = MapperConfiguration.class)
public interface ProductMapper {

    ProductMapper INSTANCE = Mappers.getMapper(ProductMapper.class);

    ProductDTO toDTO(Product product);

    List<ProductDTO> toProductDTOList(List<Product> products);

}
