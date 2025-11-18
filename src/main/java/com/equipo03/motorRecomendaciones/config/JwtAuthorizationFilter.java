package com.equipo03.motorRecomendaciones.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;


public class JwtAuthorizationFilter extends OncePerRequestFilter {

    //Dependencia para trabajar con tokens JWT
    private final JwtUtil jwtUtil;

    //Dpendencia para cargar los detalles del usuario 
    private final CustomUserDetailsService userDetailsService;


    /**
     * Construuctor de la clase JwtAuthorizationFilter para cargar las dependencias.
     */

     public JwtAuthorizationFilter(JwtUtil jwtUtil, CustomUserDetailsService userDetailsService){
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
     }

     /**
      * Método que es ejecutado en cada petición HTTP para validar el token JWT.
      1-Obtenemos el token JWT de la cabecera Authorization de la petición.
      2-Validamos el token utilizando {@link JwtUtil}.
      3-Si el token es válido, extraemos el nombre de usuario del token y creamos un objeto de autenticación.
      4-Establecemos el objeto de autenticación en el contexto de seguridad de Spring
      5-Continuamos con la cadena de filtros
      @param request HTTP request
      @param response HTTP response
      @param filterChain cadena de filtros 
      @throws ServletException si ocurre un error en el servlet
      @throws IOException si ocurre un error de entrada/salida

      */
     @Override
     protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) 
     throws ServletException, IOException {
        
        //1.Obtenemos el token JWT de la cabecera Authorization de la petición
        String token = getJwtFromRequest(request);
        //Validamos el token 
        if(token != null && jwtUtil.validateToken(token)){
            String username = jwtUtil.getUsernameFromJwt(token);
            UserDetails userDet = userDetailsService.loadUserByUsername(username);

            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                userDet,
                null,
                userDet.getAuthorities());
            auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(auth);
            }
            filterChain.doFilter(request, response);
        }

     
    
    /**
     * Método encargado de extraer el token JWT de la cabecera Authorization de la petición HTTP.
     * 1-Obtenemos el valor de la cabecera Authorization.
     * 2-Si la cabecera no es nula y comienza con el prefijo
     *  "Bearer ", extraemos el token eliminando el prefijo.
     * 3-Devolvemos el token extraído o null si no se encuentra.
     * @param request HTTP request
     * @return token JWT o null si no se encuentra
     * 
     */
     private String getJwtFromRequest(HttpServletRequest request) {
    String bearerToken = request.getHeader("Authorization");
    if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
      return bearerToken.substring(7);
    }
    return null;
  }
}