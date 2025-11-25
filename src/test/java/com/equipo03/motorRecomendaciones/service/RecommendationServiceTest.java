package com.equipo03.motorRecomendaciones.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import java.time.Instant;
import java.util.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import com.equipo03.motorRecomendaciones.dto.ProductDTO;
import com.equipo03.motorRecomendaciones.dto.records.DatosProducto;
import com.equipo03.motorRecomendaciones.mapper.ProductMapper;
import com.equipo03.motorRecomendaciones.model.Product;
import com.equipo03.motorRecomendaciones.model.Rating;
import com.equipo03.motorRecomendaciones.model.Recommendation;
import com.equipo03.motorRecomendaciones.model.User;
import com.equipo03.motorRecomendaciones.repository.ProductRepository;
import com.equipo03.motorRecomendaciones.repository.RatingRepository;
import com.equipo03.motorRecomendaciones.repository.RecommendationRepository;
import com.equipo03.motorRecomendaciones.repository.UserRepository;
import com.equipo03.motorRecomendaciones.service.impl.RecomendationServiceImpl;

@ExtendWith(MockitoExtension.class)
class RecommendationServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RatingRepository ratingRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private RecommendationRepository recommendationRepository;

    @Mock
    private ProductMapper productMapper;

    @Spy
    @InjectMocks
    private RecomendationServiceImpl service;

    private UUID userId;
    private User sampleUser;
    private Recommendation sampleRecommendation;
    private List<Product> sampleProducts;
    private List<ProductDTO> sampleProductDTOs;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();

        sampleUser = User.builder()
                .id(userId)
                .username("testuser")
                .email("test@example.com")
                .build();

        Product product1 = Product.builder().id(UUID.randomUUID()).name("Producto1").build();
        Product product2 = Product.builder().id(UUID.randomUUID()).name("Producto2").build();
        sampleProducts = List.of(product1, product2);

        sampleRecommendation = Recommendation.builder()
                .user(sampleUser)
                .products(sampleProducts)
                .algorithmVersion("v1-hibrido-tags-rating-popularidad")
                .build();

        ProductDTO dto1 = ProductDTO.builder().id(product1.getId()).name(product1.getName()).build();
        ProductDTO dto2 = ProductDTO.builder().id(product2.getId()).name(product2.getName()).build();
        sampleProductDTOs = List.of(dto1, dto2);
    }

    @Test
    void getRecommendations_devuelveListaDeDTOs_cuandoUsuarioExiste() {
        // Mock: el usuario existe
        when(userRepository.findById(userId)).thenReturn(Optional.of(sampleUser));

        // Mock: calcularYGuardarRecomendacion devuelve nuestra recomendación de prueba
        when(service.calcularYGuardarRecomendacion(sampleUser)).thenReturn(sampleRecommendation);

        // Mock: el mapper convierte los productos en DTOs
        when(productMapper.toProductDTOList(sampleProducts)).thenReturn(sampleProductDTOs);

        // Act: llamamos al método
        List<ProductDTO> result = service.getRecommendations(userId);

        // Assert: verificamos resultados
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Producto1", result.get(0).getName());
        assertEquals("Producto2", result.get(1).getName());

        // Verificar interacciones
        verify(userRepository).findById(userId);
        verify(productMapper).toProductDTOList(sampleProducts);
    }

    @Test
    void getRecommendations_lanzaExcepcion_cuandoUsuarioNoExiste() {
        // Mock: usuario no existe
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> service.getRecommendations(userId));

        assertEquals("Usuario no encontrado", ex.getMessage());
    }

    @Test
    void calcularYGuardarRecomendacion_devuelveRecomendacion_cuandoDatosValidos() {
        // ----- Preparar datos -----
        User user = sampleUser;

        // Mock de valoraciones del usuario
        Product ratedProduct = Product.builder().id(UUID.randomUUID()).name("ProductoValorado").build();
        Rating rating = Rating.builder().user(user).product(ratedProduct).score(5).build();
        List<Rating> ratingsUsuario = List.of(rating);
        when(ratingRepository.findByUserId(user.getId())).thenReturn(ratingsUsuario);

        // Mock de productos candidatos
        Product product1 = Product.builder().id(UUID.randomUUID()).name("Producto1").build();
        Product product2 = Product.builder().id(UUID.randomUUID()).name("Producto2").build();
        List<Product> candidatos = List.of(product1, product2);
        when(productRepository.findAll()).thenReturn(candidatos);

        // Mock de métricas agregadas como List<Object[]>
        List<Object[]> promediosMock = List.of(
                new Object[] { product1.getId(), 4.5 },
                new Object[] { product2.getId(), 3.8 });
        when(ratingRepository.obtenerPromedioRatingPorIdsProductos(anyList())).thenReturn(promediosMock);

        List<Object[]> cantidadesMock = List.of(
                new Object[] { product1.getId(), 10L },
                new Object[] { product2.getId(), 5L });
        when(ratingRepository.obtenerCantidadValoracionesPorIdsProductos(anyList())).thenReturn(cantidadesMock);

        RecomendationServiceImpl spyService = service;
        doReturn(Set.of("tag1", "tag2")).when(spyService).extraerTagsUsuario(ratingsUsuario);
        doReturn(1L).when(spyService).calcularCoincidenciasTags(any(), any());
        doReturn(1.0).when(spyService).normalizarYCombinarScore(any());
        doReturn(List.of(product1, product2)).when(spyService)
                .filtrarYSeleccionarTop(anyList(), anySet(), eq(service.DEFAULT_LIMIT));

        Recommendation recommendationSaved = Recommendation.builder()
                .user(user)
                .products(candidatos)
                .algorithmVersion("v1-hibrido-tags-rating-popularidad")
                .build();
        when(recommendationRepository.save(any(Recommendation.class))).thenReturn(recommendationSaved);

        // ----- Act -----
        Recommendation result = spyService.calcularYGuardarRecomendacion(user);

        // ----- Assert -----
        assertNotNull(result);
        assertEquals(user, result.getUser());
        assertEquals(2, result.getProducts().size());
        assertTrue(result.getProducts().contains(product1));
        assertTrue(result.getProducts().contains(product2));

        // Verificar interacciones
        verify(ratingRepository).findByUserId(user.getId());
        verify(productRepository).findAll();
        verify(recommendationRepository).save(any(Recommendation.class));
    }

    @Test
    void extraerTagsUsuario_devuelveConjuntoTags_normalizadosYFiltrados() {
        // Producto con tags normales
        Product product1 = Product.builder()
                .tags(new HashSet<>(List.of("Tag1", " Tag2 ")))
                .build();

        // Producto con tags, incluyendo null
        Set<String> tagsProduct2 = new HashSet<>();
        tagsProduct2.add("TAG2");
        tagsProduct2.add("tag3");
        tagsProduct2.add(null); // null permitido
        Product product2 = Product.builder().tags(tagsProduct2).build();

        // Producto con tags nulos
        Product productNullTags = Product.builder().tags(null).build();

        // Valoraciones
        Rating r1 = Rating.builder().score(5).product(product1).build();
        Rating r2 = Rating.builder().score(4).product(product2).build();
        Rating r3 = Rating.builder().score(2).product(product1).build(); // score bajo, no se incluye
        Rating r4 = Rating.builder().score(5).product(productNullTags).build();

        List<Rating> ratings = List.of(r1, r2, r3, r4);

        // Act
        Set<String> result = service.extraerTagsUsuario(ratings);

        // Assert: solo tags de ratings positivos y no nulos
        assertEquals(3, result.size());
        assertTrue(result.contains("tag1"));
        assertTrue(result.contains("tag2"));
        assertTrue(result.contains("tag3"));
    }

    @Test
    void calcularCoincidenciasTags_cuentaCorrectamenteTagsCoincidentes() {
        Product product = Product.builder()
                .tags(new HashSet<>(Arrays.asList("Tag1", "tag2", "Tag3", null)))
                .build();

        Set<String> userTags = new HashSet<>(Arrays.asList("tag1", "tag3", "tagX"));

        long coincidencias = service.calcularCoincidenciasTags(product, userTags);

        // Solo tag1 y tag3 coinciden (case insensitive)
        assertEquals(2, coincidencias);
    }

    @Test
    void calcularCoincidenciasTags_devuelveCero_siNoHayTags() {
        Product product1 = Product.builder().tags(null).build();
        Product product2 = Product.builder().tags(Set.of()).build();

        assertEquals(0L, service.calcularCoincidenciasTags(product1, Set.of("tag1")));
        assertEquals(0L, service.calcularCoincidenciasTags(product2, Set.of("tag1")));
        assertEquals(0L, service.calcularCoincidenciasTags(product2, Set.of()));
        assertEquals(0L, service.calcularCoincidenciasTags(product2, null));
    }

    @Test
    void normalizarYCombinarScore_calculaScoreCorrectamente() {
        // Ejemplo de métricas
        DatosProducto datos = new DatosProducto(2L, 4.0, 50L);

        // Se calcula según los pesos definidos en la clase
        double score = service.normalizarYCombinarScore(datos);

        // Verificar que el resultado esté entre 0 y 1
        assertTrue(score >= 0.0 && score <= 1.0);
    }

    @Test
    void normalizarYCombinarScore_devuelveCero_siSumaPesosEsCero() {
        double originalTagWeight = service.TAG_WEIGHT;
        double originalRatingWeight = service.RATING_WEIGHT;
        double originalPopularityWeight = service.POPULARITY_WEIGHT;

        service.TAG_WEIGHT = 0.0;
        service.RATING_WEIGHT = 0.0;
        service.POPULARITY_WEIGHT = 0.0;

        DatosProducto datos = new DatosProducto(10L, 5.0, 100L);
        double score = service.normalizarYCombinarScore(datos);

        assertEquals(0.0, score);

        // Restaurar pesos originales
        service.TAG_WEIGHT = originalTagWeight;
        service.RATING_WEIGHT = originalRatingWeight;
        service.POPULARITY_WEIGHT = originalPopularityWeight;
    }

}
