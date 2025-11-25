package com.equipo03.motorRecomendaciones.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class JwtAuthorizationFilter extends OncePerRequestFilter {

  private final JwtUtil jwtUtil;
  private final CustomUserDetailsService userDetailsService;

  public JwtAuthorizationFilter(JwtUtil jwtUtil, CustomUserDetailsService userDetailsService) {
    this.jwtUtil = jwtUtil;
    this.userDetailsService = userDetailsService;
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request,
      HttpServletResponse response,
      FilterChain filterChain)
      throws ServletException, IOException {

    // Excluir rutas públicas (login, register, swagger, docs)
    String path = request.getRequestURI();
    if (path.startsWith("/api/auth/login")
        || path.startsWith("/api/auth/register")
        || path.startsWith("/swagger-ui")
        || path.startsWith("/api-docs")) {
      filterChain.doFilter(request, response);
      return;
    }

    String token = getJwtFromRequest(request);

    if (token != null) {
      try {
        if (jwtUtil.validateToken(token)) {
          String username = jwtUtil.getUsernameFromJwt(token);
          String role = jwtUtil.getRoleFromToken(token);

          UserDetails userDet = userDetailsService.loadUserByUsername(username);

          List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_" + role));

          UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(userDet, null,
              authorities);

          auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
          SecurityContextHolder.getContext().setAuthentication(auth);
        }
      } catch (Exception e) {
        System.out.println(">>> ERROR AL PROCESAR EL TOKEN: " + e.getMessage());
        e.printStackTrace();
      }
    }

    filterChain.doFilter(request, response);
  }

  private String getJwtFromRequest(HttpServletRequest request) {
    String bearerToken = request.getHeader("Authorization");
    if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
      return bearerToken.substring(7);
    }
    return null;
  }
}