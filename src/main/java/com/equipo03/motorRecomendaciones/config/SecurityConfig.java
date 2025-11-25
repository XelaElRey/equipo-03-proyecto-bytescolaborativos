package com.equipo03.motorRecomendaciones.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.authentication.password.CompromisedPasswordChecker;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.password.HaveIBeenPwnedRestApiPasswordChecker;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Configuracón de seguridad de la aplicación.
 * -Define los beans necesarios para la seguridad.
 * {PasswordEncoder, SecurityFilterChain,AuthenticationManager}
 * -Configura las reglas de seguridad HTTP.
 * {rutas públicas y protegidas, filtros de seguridad, gestión de sesiones,
 * CSRF}
 * -Añadimos filtros de seguridad personalizados para la autentiación y
 * autorización mediante JWT.
 * 
 * @author Alex
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(CustomUserDetailsService userDetailsService) {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return new ProviderManager(authProvider);
    }

    public JwtAuthorizationFilter jwtAuthorizationFilter(JwtUtil jwtUtil, CustomUserDetailsService userDetailsService) {
        return new JwtAuthorizationFilter(jwtUtil, userDetailsService);
    }

    @Bean
    public CompromisedPasswordChecker compromisedPasswordChecker() {
        return new HaveIBeenPwnedRestApiPasswordChecker();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtAuthorizationFilter jwtAuthorizationFilter)
            throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // Rutas públicas
                        .requestMatchers("/api/auth/login", "/api/auth/register",
                                "/swagger-ui/**", "/swagger-ui.html",
                                "/api-docs", "/api-docs/**")
                        .permitAll()

                        // Endpoints protegidos por rol
                        .requestMatchers("/api/tournaments/**").hasRole("ADMIN")
                        .requestMatchers("/api/ratings/**").hasRole("PLAYER")
                        .requestMatchers("/recommendations/**").authenticated()

                        // Cualquier otra ruta requiere autenticación
                        .anyRequest().authenticated())

                // Manejo de errores de autenticación
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            response.setContentType("application/json");
                            response.getWriter().write("{\"error\": \"No autorizado\"}");
                        }))

                // Filtro JWT antes del filtro de autenticación de Spring
                .addFilterBefore(jwtAuthorizationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

}
