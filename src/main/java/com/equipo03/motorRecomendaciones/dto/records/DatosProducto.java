package com.equipo03.motorRecomendaciones.dto.records;

/**
 * Record que agrupa las métricas relevantes de un producto para el cálculo del score de recomendación.
 *
 * @param coincidenciasTags Número de tags del producto que coinciden con los intereses del usuario
 * @param promedioRating    Rating promedio del producto (0 a 5)
 * @param popularidad       Popularidad del producto (por ejemplo, número de valoraciones)
 */
public record DatosProducto(long coincidenciasTags, double promedioRating, long popularidad) {}
