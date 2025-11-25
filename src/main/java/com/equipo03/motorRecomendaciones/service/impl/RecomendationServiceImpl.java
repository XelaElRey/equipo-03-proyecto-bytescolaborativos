package com.equipo03.motorRecomendaciones.service.impl;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.equipo03.motorRecomendaciones.dto.ProductDTO;
import com.equipo03.motorRecomendaciones.dto.records.DatosProducto;
import com.equipo03.motorRecomendaciones.dto.records.ProductoPuntuado;
import com.equipo03.motorRecomendaciones.mapper.ProductMapper;
import com.equipo03.motorRecomendaciones.model.Product;
import com.equipo03.motorRecomendaciones.model.Rating;
import com.equipo03.motorRecomendaciones.model.Recommendation;
import com.equipo03.motorRecomendaciones.model.User;
import com.equipo03.motorRecomendaciones.repository.ProductRepository;
import com.equipo03.motorRecomendaciones.repository.RatingRepository;
import com.equipo03.motorRecomendaciones.repository.RecommendationRepository;
import com.equipo03.motorRecomendaciones.repository.UserRepository;
import com.equipo03.motorRecomendaciones.service.RecommendationService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;

@Service
@RequiredArgsConstructor
public class RecomendationServiceImpl implements RecommendationService {

    // Repositorios necesarios
    private final UserRepository userRepository;
    private final RatingRepository ratingRepository;
    private final ProductRepository productRepository;
    private final RecommendationRepository recommendationRepository;
    private final ProductMapper productMapper;

    // Pesos y configuraciones inyectadas desde application.yaml
    @Value("${recommendation.tag-weight}")
    public double TAG_WEIGHT;

    @Value("${recommendation.rating-weight}")
    public double RATING_WEIGHT;

    @Value("${recommendation.popularity-weight}")
    public double POPULARITY_WEIGHT;

    @Value("${recommendation.default-limit}")
    public int DEFAULT_LIMIT;

    @Value("${recommendation.positive-rating-threshold}")
    public int POSITIVE_RATING_THRESHOLD;

    // -------------------------
    // Método principal expuesto al controlador
    // -------------------------
    @Override
    @Transactional
    public List<ProductDTO> getRecommendations(UUID userId) {
        // 1) Verificar que el usuario exista
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // 2) Calcular y guardar la recomendación en la base de datos
        Recommendation recomendacion = calcularYGuardarRecomendacion(user);

        // 3) Mapear los productos recomendados a DTOs y devolverlos al cliente
        return productMapper.toProductDTOList(recomendacion.getProducts());
    }

    // -------------------------
    // Método principal del motor de recomendaciones
    // -------------------------
    public Recommendation calcularYGuardarRecomendacion(User user) {

        // 1) Obtener todas las valoraciones que el usuario ha hecho
        List<Rating> ratingsUsuario = ratingRepository.findByUserId(user.getId());

        // 2) Extraer los tags relevantes a partir de las valoraciones positivas
        Set<String> tagsUsuario = extraerTagsUsuario(ratingsUsuario);

        // 3) Obtener todos los productos candidatos
        List<Product> candidatos = productRepository.findAll();

        // 4) Crear lista de IDs de los productos candidatos para consultas agregadas
        List<UUID> idsCandidatos = candidatos.stream()
                .map(Product::getId)
                .toList();

        // 5) Obtener métricas agregadas de los productos (rating promedio y cantidad de
        // valoraciones)
        Map<UUID, Double> promedioRatingPorProducto = convertirDoubleMap(
                ratingRepository.obtenerPromedioRatingPorIdsProductos(idsCandidatos));

        Map<UUID, Long> cantidadValoracionesPorProducto = convertirALongMap(
                ratingRepository.obtenerCantidadValoracionesPorIdsProductos(idsCandidatos));

        // 6) Excluir productos que el usuario ya valoró para no recomendarlos de nuevo
        Set<UUID> productosExcluidos = ratingsUsuario.stream()
                .map(r -> r.getProduct().getId())
                .collect(Collectors.toSet());

        // 7) Calcular score combinado para cada producto candidato
        List<ProductoPuntuado> puntuados = candidatos.stream()
                .map(p -> {
                    // Calcular coincidencias de tags con los intereses del usuario
                    long coincidenciasTags = calcularCoincidenciasTags(p, tagsUsuario);

                    // Obtener rating promedio del producto
                    double promedioRating = promedioRatingPorProducto.getOrDefault(p.getId(), 0.0);

                    // Obtener popularidad del producto
                    long popularidad = cantidadValoracionesPorProducto.getOrDefault(
                            p.getId(),
                            Optional.ofNullable(p.getPopularityScore()).orElse(0L));

                    // Guardar las métricas en un objeto auxiliar
                    DatosProducto datos = new DatosProducto(coincidenciasTags, promedioRating, popularidad);

                    // Calcular el score final normalizado y ponderado
                    double score = normalizarYCombinarScore(datos);

                    // Devolver producto y score
                    return new ProductoPuntuado(p, score);
                })
                // Ordenar productos de mayor a menor score
                .sorted(Comparator.comparingDouble(ProductoPuntuado::score).reversed())
                .toList();

        // 8) Filtrar productos ya valorados y seleccionar solo los top N
        List<Product> recomendados = filtrarYSeleccionarTop(puntuados, productosExcluidos, DEFAULT_LIMIT);

        // 9) Crear la entidad Recommendation y guardarla en la base de datos
        Recommendation recomendacion = Recommendation.builder()
                .user(user)
                .products(recomendados)
                .algorithmVersion("v1-hibrido-tags-rating-popularidad")
                .computedAt(Instant.now())
                .build();

        return recommendationRepository.save(recomendacion);
    }

    // -------------------------
    // Métodos auxiliares
    // -------------------------

    /**
     * Extrae los tags relevantes para un usuario a partir de sus valoraciones
     * positivas.
     * Solo se consideran los productos que el usuario calificó por encima del
     * umbral
     * definido por POSITIVE_RATING_THRESHOLD.
     *
     * @param ratingsUsuario Lista de valoraciones del usuario
     * @return Conjunto de tags normalizados (trim y lowercase) que representan
     *         intereses del usuario
     */
    public Set<String> extraerTagsUsuario(List<Rating> ratingsUsuario) {
        // Si no hay valoraciones, devolvemos un conjunto vacío
        if (ratingsUsuario == null || ratingsUsuario.isEmpty())
            return Collections.emptySet();

        // Procesamos las valoraciones:
        return ratingsUsuario.stream()
                // Filtramos solo ratings positivos (por encima del umbral)
                .filter(r -> r.getScore() >= POSITIVE_RATING_THRESHOLD)
                // Obtenemos el producto de cada rating
                .map(Rating::getProduct)
                // Eliminamos productos nulos
                .filter(Objects::nonNull)
                // Obtenemos todos los tags del producto y los convertimos en un stream
                .flatMap(p -> p.getTags() == null ? Stream.empty() : p.getTags().stream())
                // Eliminamos tags nulos
                .filter(Objects::nonNull)
                // Limpiamos espacios innecesarios
                .map(String::trim)
                // Convertimos a minúsculas para normalizar comparaciones
                .map(String::toLowerCase)
                // Colectamos en un conjunto, eliminando duplicados automáticamente
                .collect(Collectors.toSet());
    }

    /**
     * Calcula cuántos tags del producto coinciden con los tags de interés del
     * usuario.
     * Este valor se usa posteriormente para ponderar la relevancia del producto en
     * la recomendación.
     *
     * @param product     Producto a evaluar
     * @param tagsUsuario Conjunto de tags que representan los intereses del usuario
     * @return Número de tags que coinciden entre el producto y el usuario (long)
     */
    public long calcularCoincidenciasTags(Product product, Set<String> tagsUsuario) {
        // Si el usuario no tiene tags, o no hay tags en el producto, devolvemos 0
        if (tagsUsuario == null || tagsUsuario.isEmpty() || product.getTags() == null)
            return 0L;

        // Recorremos los tags del producto:
        // - Ignoramos elementos nulos
        // - Eliminamos espacios innecesarios
        // - Convertimos a minúsculas para hacer la comparación case-insensitive
        // Contamos cuántos de estos tags coinciden con los tags del usuario
        return product.getTags().stream()
                .filter(Objects::nonNull)
                .map(String::trim)
                .map(String::toLowerCase)
                .filter(tagsUsuario::contains)
                .count();
    }

    /**
     * Normaliza las métricas individuales de un producto (tags, rating,
     * popularidad)
     * y combina los resultados en un score final ponderado entre 0 y 1.
     * Este score se usa para ordenar y seleccionar productos recomendados.
     *
     * @param datos Objeto que contiene las métricas del producto:
     *              - coincidenciasTags: número de tags que coinciden con el usuario
     *              - promedioRating: rating promedio del producto (0-5)
     *              - popularidad: cantidad de valoraciones o score de popularidad
     * @return Score final normalizado y ponderado entre 0.0 y 1.0
     */
    public double normalizarYCombinarScore(DatosProducto datos) {
        // Normalizamos la cantidad de coincidencias de tags a [0,1]
        double scoreTags = normalizarTags(datos.coincidenciasTags());

        // Normalizamos el rating promedio a [0,1]
        double scoreRating = normalizarRating(datos.promedioRating());

        // Normalizamos la popularidad del producto a [0,1]
        double scorePopularidad = normalizarPopularidad(datos.popularidad());

        // Combinamos las métricas usando los pesos definidos para cada criterio
        double ponderado = scoreTags * TAG_WEIGHT
                + scoreRating * RATING_WEIGHT
                + scorePopularidad * POPULARITY_WEIGHT;

        // Calculamos la suma de pesos para normalizar el score final
        double sumaPesos = TAG_WEIGHT + RATING_WEIGHT + POPULARITY_WEIGHT;

        // Devolvemos el score final normalizado, asegurando que no haya división por
        // cero
        return (sumaPesos == 0) ? 0.0 : (ponderado / sumaPesos);
    }

    /**
     * Normaliza el rating promedio de un producto a un valor entre 0 y 1,
     * Se asume que los ratigns van de 0 a 5, por lo que se divide entre 5
     */
    public double normalizarRating(double promedio) {
        // Dividimos el rating entre 5 para escalar al rango [0,1]
        // Math.min asegura que no se supere 1.0
        // Math.max asegura que no sea menor que 0.0
        return Math.max(0.0, Math.min(1.0, promedio / 5.0));
    }

    /**
     * Normaliza la cantidad de coincidencias de tags de un producto con los
     * intereses del usuario
     * a un valor entre 0 y 1.
     * Se aplica una saturación máxima para que más de 5 coincidencias no aumenten
     * el score.
     */
    public double normalizarTags(long coincidencias) {
        // Definimos el número de coincidencias a partir del cual saturamos el score
        final double SAT = 5.0;
        // Escalamos el valor al rango [0,1] y saturamos si excede el máximo
        return Math.max(0.0, Math.min(1.0, coincidencias / SAT));
    }

    /**
     * Normaliza popularidad de un producto a un valor entre 0 y 1.
     * Se aplica logaritmo para reducir el impacto de productos extremadamente
     * populares
     * y se divide por un valor máximo estimado para escalar la puntuación.
     */
    public double normalizarPopularidad(long popularidad) {
        // Aplicamos logaritmo (productos con muchas valoraciones no dominan totalmente
        // el score)
        double val = Math.log1p(popularidad);
        // Estimación del valor máximo de popularidad para noramalizar
        double MAX_EST = Math.log1p(1000);
        // Escalamos el valor al rango [0,1] y aseguramos que no exceda los límites
        return Math.max(0.0, Math.min(1.0, val / MAX_EST));
    }

    /**
     * Método creado para obtener una lista de productos posibles para recomendar.
     * 1-Recorremos la lista de productos puntuado, excluyendo los productos
     * oportunos
     * 2-Convertimos cada ProductoPuntuado en un Product
     * 3-Tomamos los primeros 10 Product del stream
     * 4-Convertimos el Stream en una List<Product>
     * 
     * @param puntuados
     * @param excluidos
     * @param limite
     * @return List<Product>
     */
    public List<Product> filtrarYSeleccionarTop(List<ProductoPuntuado> puntuados, Set<UUID> excluidos, int limite) {
        return puntuados.stream()
                .filter(p -> !excluidos.contains(p.producto().getId()))
                .map(ProductoPuntuado::producto)
                .limit(limite)
                .toList();
    }

    /**
     * Convierte los resultados de una consulta que devuelve un promedio de ratings
     * en un Map donde la clave es el UUID del producto y el valor es el promedio
     * como Double
     * 
     * @param filas : list de Object[], cada fila con [idProducto, promedioRating]
     * @return Map<UUID, Double> con el ID del producto y el promedio de rating
     */
    public Map<UUID, Double> convertirDoubleMap(List<Object[]> filas) {
        // Si la lista de filas es null devolvemos un Map vacío para evitar
        // NullPointerException
        if (filas == null)
            return Collections.emptyMap();
        // Creamos un HashMap donde almacenaremos los valores
        Map<UUID, Double> map = new HashMap<>();
        // Recorremos cada fila de la consulta
        for (Object[] fila : filas) {
            // Ignoramos filas nulas o incompletas
            if (fila == null || fila.length < 2)
                continue;
            // Convertimos el primer elemento de la fila a UUID (ID del Producto)
            UUID id = (UUID) fila[0];
            // Convertimos el segundo elemento a Number (promedio de Rating)
            Number n = (Number) fila[1];
            // Guardamos en el Map, si el número es null usamos 0.0 como valor por defecto
            map.put(id, n == null ? 0.0 : n.doubleValue());
        }
        // Devolvemos el Map final con todos los promedios de Ratign
        return map;
    }

    /**
     * Convierte los resultados de una consulta que devuelve la cantidad de
     * valoraciones
     * de productos en un Map donde la clave es el UUID del Producto y el valor es
     * la canitdad como Long.
     * 
     * @param filas Lista de Object[], cada fila con [idProducto,
     *              cantidadValoraciones]
     * @return Map<UUID, Long> con ID de producto → cantidad de valoraciones
     */
    public Map<UUID, Long> convertirALongMap(List<Object[]> filas) {
        // Si la lista de filas es null, devolvemos un Map vacío para evitar
        // NullPointerException
        if (filas == null)
            return Collections.emptyMap();
        // Creamos un HashMap donde se almacenarán los resultados
        Map<UUID, Long> map = new HashMap<>();
        // Recorremos cada fila de la consulta
        for (Object[] fila : filas) {
            // Ignoramos filas nulas o incompletas
            if (fila == null || fila.length < 2)
                continue;
            // Convertimos el primer elementos de cada fila en UUID
            UUID id = (UUID) fila[0];
            // Convertimos el segundo elemento de la fila en Number
            Number n = (Number) fila[1];
            // Guardamos en el Map, si el valor es null usamos 0L como valor por defecto
            map.put(id, n == null ? 0L : n.longValue());
        }
        // Devolvemos el Map final con todas las cantidades de valoraciones
        return map;
    }

}
