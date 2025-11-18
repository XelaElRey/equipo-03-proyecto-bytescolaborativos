package com.equipo03.motorRecomendaciones.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.Date;
import org.springframework.stereotype.Component;

/**
 * Clase encargada de generar token JWT. 
 * -Genera tokens JWT firmados con una clave secreta.
 * -Valida tokens JWT y extrae información de estos.
 * 
 * @author Alex
 */
@Component
public class JwtUtil{

    //Clave secreta para firmar tokens jwt
    @Value("${jwt.secret-key}")
    private String secret;

    @Value("${jwt.access-token-expiration}")
    private Long expiration;

    /**
     * Método encargado de generar un token JWT para un usuario autenticado.
     * 1-Extrae el nomnbre de usuario del objeto de autenticación pasado por parámetro.
     * 2-Genera un token con el nombre usuario,la fecha de creación y la fecha de expiración.
     * 3-Firmamos el token con la clave secreta utilizando el algoritmo de firma HS256.
     * 4-Devolvemos el token generado.
     * 
     * @param auth
     * @return token 
     */
   public String generateToken(Authentication auth) {
    String username = auth.getName();
    Date now = new Date();
    Date exp = new Date(now.getTime() + expiration);
    return Jwts.builder()
        .setSubject(username)
        .setIssuedAt(now)
        .setExpiration(exp)
        .signWith(SignatureAlgorithm.HS256, secret)
        .compact();
  }

  /**
   * Método encargado de obtener el nombre de usuario desde un token JWT.
   * 1-Parseamos el token utilizando la clave secreta para validar su firma.
   * 2-Extraemos el cuerpo del token (claims) y obtenemos el nombre de usuario (subject).
   * 3-Devolvemos el nombre de usuario extraído.
   * 
   * @param token
   * @return username
   */
    public String getUsernameFromJwt(String token){
        
        Claims claims = Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
        return claims.getSubject();
    }

    /**
     * Método encargado de validar un token JWT.
     * 1-Intentamos parsear el token utilizando la clave secreta.
     * 2-Si el parseo es exitoso, el token es válido y devolvemos true;
     * 3-Si ocurre alguna excepción durante el parseo, el token no es válido y devolvemos false.
     * 
     * @retunr boolean
     */
    public boolean validateToken(String token){
        try{
            Jwts.parser().setSigningKey(secret).parseClaimsJws(token);
            return true;
        }catch(Exception e){
            return false;
        }
    }

    

}