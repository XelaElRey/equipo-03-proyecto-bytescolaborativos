package com.equipo03.motorRecomendaciones.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Swagger/OpenApi clase de configuración. Esta clase configura automáticamente la documentación de la API utilizando Swagger.
 */
@Configuration
public class SwaggerConfig {
    
/**
 * Configura la documentación de la API mediante OpenAPi con los requerimientos básicos.
 */

 @Bean 
 public OpenAPI customOpenAPI() {
    return new OpenAPI()
        .info(
            new Info()
                .title("Motor de Recomendaciones API") // Título de la API
                .version("1.0") 
                .description("Documentación de la API del motor de recomendaciones")) // Descripción de la API
        .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
        .components(
            new Components()
                .addSecuritySchemes(
                    "bearerAuth",
                    new SecurityScheme()
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT")));
  }
}
