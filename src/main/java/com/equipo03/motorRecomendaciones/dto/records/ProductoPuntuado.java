package com.equipo03.motorRecomendaciones.dto.records;

import com.equipo03.motorRecomendaciones.model.Product;

/**
 * Record que asocia un producto con su score calculado.
 * Este score se usa para ordenar y seleccionar los productos recomendados.
 *
 * @param producto Producto original
 * @param score    Score final normalizado entre 0 y 1
 */
public record ProductoPuntuado(Product producto, double score) {}
