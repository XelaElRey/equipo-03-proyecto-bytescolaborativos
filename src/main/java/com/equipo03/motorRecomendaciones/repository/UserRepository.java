package com.equipo03.motorRecomendaciones.repository;

import java.util.Optional;
import com.equipo03.motorRecomendaciones.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    //Obtener el usuario por su nombre
    Optional<User> findByUsername(String username);
    //Obtener el usuario por su email
    Optional<User> findByEmail(String email);

}
