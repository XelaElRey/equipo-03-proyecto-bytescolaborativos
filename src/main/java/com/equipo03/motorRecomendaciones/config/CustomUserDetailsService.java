package com.equipo03.motorRecomendaciones.config;

import com.equipo03.motorRecomendaciones.model.User;
import com.equipo03.motorRecomendaciones.repository.UserRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    // Repositorio para acceder a los datos de usuario
    private final UserRepository userRepository;

    /**
     * Constructor de la clase para inyecta el repositorio de usuarios.
     */
    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Método encargado de cargar los detalle de un usuario por su nombre de
     * usuario.
     * 1-Busca el usuario en la base de datos utilizando el {@link UserRepository}.
     * 2-Si el usuario no es encontrado, @throws @UsernameNotFoundException.
     * 3-Si el usuario existe, convierte el objeto User obtenido en un objeto
     * UserDetails.
     * 
     * @param Username nombre de usuario.
     * @return UserDetails detalles del usuario.
     * @throws UsernameNotFoundException si el usuario no es encontrado.
     */

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User userEntity = userRepository
                .findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("No se encontró el usuario: " + username));

        if (userEntity.getRole() == null) {
            throw new UsernameNotFoundException("Usuario sin rol asignado: " + username);
        }

        String roleName = "ROLE_" + userEntity.getRole().name();

        return org.springframework.security.core.userdetails.User.builder()
                .username(userEntity.getUsername())
                .password(userEntity.getPassword())
                .authorities(roleName)
                .disabled(!userEntity.isActive())
                .build();
    }

}