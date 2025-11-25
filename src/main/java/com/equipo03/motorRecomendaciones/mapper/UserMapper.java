package com.equipo03.motorRecomendaciones.mapper;

import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import com.equipo03.motorRecomendaciones.dto.UserDTO;
import com.equipo03.motorRecomendaciones.dto.request.UserRequestDto;
import com.equipo03.motorRecomendaciones.model.User;

@Mapper(config = MapperConfiguration.class)
public interface UserMapper {

    /**
     * Convierte una entidad User en un objeto UserDTO para las respuestas de la API
     */
    UserDTO userToUserDTO(User user);

    /**
     * Convierte un objeto UserDTO en una entidad User
     * 
     * @param userDTO
     * @return entidad User
     * @throws IllegalArgumentException
     * 
     */
    User userDtoToUser(UserDTO userDTO);

    /**
     * 
     * @param listUserDTO
     * @return
     */
    List<User> userDTOListToUserList(List<UserDTO> listUserDTO);

    /**
     * 
     * @param listUser
     * @return
     */
    List<UserDTO> userListToUserDtoList(List<User> listUser);

    @Mapping(target = "role", expression = "java(com.equipo03.motorRecomendaciones.model.enums.Role.PLAYER)")
    @Mapping(target = "active", constant = "true")
    UserDTO defaultUserDTO(UserRequestDto userRequestDTO);
}