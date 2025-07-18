package com.example.Pricing; // Asegúrate de que el paquete sea correcto para el test

import com.example.Pricing.model.Promocion;
import com.example.Pricing.model.Producto;
import com.example.Pricing.repository.PromocionRepository;
import com.example.Pricing.services.PromocionService;
import com.example.Pricing.repository.ProductoRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.NoSuchElementException; // Import para .get()

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class) // Usa MockitoExtension para pruebas unitarias puras
public class PromocionServiceTest {

    @InjectMocks // Inyecta los mocks en esta instancia del servicio
    private PromocionService promocionService;

    @Mock // Crea un mock para el repositorio de Promociones
    private PromocionRepository promocionRepository;

    @Mock // Crea un mock para el repositorio de Productos
    private ProductoRepository productoRepository;

    // Objetos de prueba
    private Producto testProducto1;
    private Producto testProducto2;
    private Promocion testPromocion1;
    private Promocion testPromocion2;

    @BeforeEach
    void setUp() {
        // Inicialización de objetos de prueba antes de cada test
        testProducto1 = new Producto();
        testProducto1.setId(101);
        testProducto1.setNombre("Producto Test 1");

        testProducto2 = new Producto();
        testProducto2.setId(102);
        testProducto2.setNombre("Producto Test 2");

        testPromocion1 = new Promocion();
        testPromocion1.setId(1);
        testPromocion1.setNombre("Promo Activa");
        testPromocion1.setFechaInicio(LocalDateTime.now().minusDays(5));
        testPromocion1.setFechaFin(LocalDateTime.now().plusDays(5));
        testPromocion1.setPorcentajeDescuento(10.0);
        testPromocion1.setActivo(true);
        testPromocion1.setProducto(testProducto1); // Asociado a un producto

        testPromocion2 = new Promocion();
        testPromocion2.setId(2);
        testPromocion2.setNombre("Promo Inactiva");
        testPromocion2.setFechaInicio(LocalDateTime.now().minusDays(10));
        testPromocion2.setFechaFin(LocalDateTime.now().minusDays(1)); // Fecha pasada
        testPromocion2.setPorcentajeDescuento(20.0);
        testPromocion2.setActivo(false);
        testPromocion2.setProducto(testProducto2); // Asociado a otro producto
    }

    @Test
    void findAll_debeRetornarListaDePromociones() {
        // Given
        when(promocionRepository.findAll()).thenReturn(Arrays.asList(testPromocion1, testPromocion2));

        // When
        List<Promocion> foundPromotions = promocionService.findAll();

        // Then
        assertNotNull(foundPromotions);
        assertEquals(2, foundPromotions.size());
        assertEquals(testPromocion1.getNombre(), foundPromotions.get(0).getNombre());
        assertEquals(testPromocion2.getNombre(), foundPromotions.get(1).getNombre());
        verify(promocionRepository, times(1)).findAll();
    }

    @Test
    void findAll_cuandoNoHayPromociones_debeRetornarListaVacia() {
        // Given
        when(promocionRepository.findAll()).thenReturn(Collections.emptyList());

        // When
        List<Promocion> foundPromotions = promocionService.findAll();

        // Then
        assertNotNull(foundPromotions);
        assertTrue(foundPromotions.isEmpty());
        verify(promocionRepository, times(1)).findAll();
    }

    @Test
    void ObtenerPromoPorId_existente_debeRetornarPromocion() {
        // Given
        when(promocionRepository.findById(testPromocion1.getId())).thenReturn(Optional.of(testPromocion1));

        // When
        Promocion foundPromo = promocionService.ObtenerPromoPorId(testPromocion1.getId());

        // Then
        assertNotNull(foundPromo);
        assertEquals(testPromocion1.getId(), foundPromo.getId());
        assertEquals(testPromocion1.getNombre(), foundPromo.getNombre());
        verify(promocionRepository, times(1)).findById(testPromocion1.getId());
    }

    @Test
    void ObtenerPromoPorId_noExistente_debeLanzarNoSuchElementException() {
        // Given
        when(promocionRepository.findById(999)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(NoSuchElementException.class, () -> promocionService.ObtenerPromoPorId(999));
        verify(promocionRepository, times(1)).findById(999);
    }

    @Test
    void obtenerPromocionesPorIdProducto_debeRetornarListaDePromociones() {
        // Given
        Integer productId = testProducto1.getId();
        when(promocionRepository.buscarPromocionesPorProductoId(productId)).thenReturn(Arrays.asList(testPromocion1));

        // When
        List<Promocion> foundPromotions = promocionService.obtenerPromocionesPorIdProducto(productId);

        // Then
        assertNotNull(foundPromotions);
        assertEquals(1, foundPromotions.size());
        assertEquals(testPromocion1.getNombre(), foundPromotions.get(0).getNombre());
        verify(promocionRepository, times(1)).buscarPromocionesPorProductoId(productId);
    }

    @Test
    void obtenerPromocionesPorIdProducto_cuandoNoHayPromociones_debeRetornarListaVacia() {
        // Given
        Integer productId = 999;
        when(promocionRepository.buscarPromocionesPorProductoId(productId)).thenReturn(Collections.emptyList());

        // When
        List<Promocion> foundPromotions = promocionService.obtenerPromocionesPorIdProducto(productId);

        // Then
        assertNotNull(foundPromotions);
        assertTrue(foundPromotions.isEmpty());
        verify(promocionRepository, times(1)).buscarPromocionesPorProductoId(productId);
    }


    @Test
    void obtenerPromosActivas_debeRetornarPromocionesActivas() {
        // Given
        when(promocionRepository.buscaPromoActiva(true)).thenReturn(Arrays.asList(testPromocion1));

        // When
        List<Promocion> activePromotions = promocionService.obtenerPromosActivas(true);

        // Then
        assertNotNull(activePromotions);
        assertEquals(1, activePromotions.size());
        assertTrue(activePromotions.get(0).isActivo());
        verify(promocionRepository, times(1)).buscaPromoActiva(true);
    }

    @Test
    void obtenerPromosActivas_debeRetornarPromocionesInactivas() {
        // Given
        when(promocionRepository.buscaPromoActiva(false)).thenReturn(Arrays.asList(testPromocion2));

        // When
        List<Promocion> inactivePromotions = promocionService.obtenerPromosActivas(false);

        // Then
        assertNotNull(inactivePromotions);
        assertEquals(1, inactivePromotions.size());
        assertFalse(inactivePromotions.get(0).isActivo());
        verify(promocionRepository, times(1)).buscaPromoActiva(false);
    }


    @Test
    void obtenerPromosPorFechaFin_debeRetornarPromociones() {
        // Given
        LocalDateTime fechaFin = testPromocion1.getFechaFin();
        when(promocionRepository.buscaPorFechaFin(fechaFin)).thenReturn(Arrays.asList(testPromocion1));

        // When
        List<Promocion> foundPromotions = promocionService.obtenerPromosPorFechaFin(fechaFin);

        // Then
        assertNotNull(foundPromotions);
        assertEquals(1, foundPromotions.size());
        assertEquals(testPromocion1.getNombre(), foundPromotions.get(0).getNombre());
        verify(promocionRepository, times(1)).buscaPorFechaFin(fechaFin);
    }

    @Test
    void obtenerPromosPorNombre_debeRetornarPromociones() {
        // Given
        String promoName = testPromocion1.getNombre();
        when(promocionRepository.buscaPorNombre(promoName)).thenReturn(Arrays.asList(testPromocion1));

        // When
        List<Promocion> foundPromotions = promocionService.obtenerPromosPorNombre(promoName);

        // Then
        assertNotNull(foundPromotions);
        assertEquals(1, foundPromotions.size());
        assertEquals(testPromocion1.getNombre(), foundPromotions.get(0).getNombre());
        verify(promocionRepository, times(1)).buscaPorNombre(promoName);
    }

    // --- Tests para guardarPromocion (más detallados para cobertura de ramas) ---

    @Test
    void guardarPromocion_nuevaPromocionConProductoExistente_debeGuardar() {
        // Given
        Promocion newPromo = new Promocion();
        newPromo.setNombre("Nueva Promo Con Prod");
        newPromo.setProducto(testProducto1); // Asignar un producto existente
        newPromo.setFechaInicio(LocalDateTime.now());
        newPromo.setFechaFin(LocalDateTime.now().plusDays(10));
        newPromo.setPorcentajeDescuento(5.0);
        newPromo.setActivo(true);

        when(productoRepository.findById(testProducto1.getId())).thenReturn(Optional.of(testProducto1));
        when(promocionRepository.save(any(Promocion.class))).thenReturn(newPromo); // Simula el guardado

        // When
        Promocion result = promocionService.guardarPromocion(newPromo);

        // Then
        assertNotNull(result);
        assertEquals("Nueva Promo Con Prod", result.getNombre());
        assertEquals(testProducto1.getId(), result.getProducto().getId());
        verify(productoRepository, times(1)).findById(testProducto1.getId());
        verify(promocionRepository, times(1)).save(newPromo);
    }

    @Test
    void guardarPromocion_nuevaPromocionSinProducto_debeGuardar() {
        // Given
        Promocion newPromo = new Promocion();
        newPromo.setNombre("Nueva Promo Sin Prod");
        newPromo.setProducto(null); // Sin producto
        newPromo.setFechaInicio(LocalDateTime.now());
        newPromo.setFechaFin(LocalDateTime.now().plusDays(10));
        newPromo.setPorcentajeDescuento(5.0);
        newPromo.setActivo(true);

        when(promocionRepository.save(any(Promocion.class))).thenReturn(newPromo); // Simula el guardado

        // When
        Promocion result = promocionService.guardarPromocion(newPromo);

        // Then
        assertNotNull(result);
        assertEquals("Nueva Promo Sin Prod", result.getNombre());
        assertNull(result.getProducto()); // Debe seguir siendo null
        verify(productoRepository, never()).findById(anyInt()); // No debe buscar producto
        verify(promocionRepository, times(1)).save(newPromo);
    }

    @Test
    void guardarPromocion_actualizarPromocionConProductoExistente_debeGuardar() {
        // Given
        testPromocion1.setNombre("Promo Actualizada"); // Modificamos el mock para la prueba
        when(productoRepository.findById(testProducto1.getId())).thenReturn(Optional.of(testProducto1));
        when(promocionRepository.save(any(Promocion.class))).thenReturn(testPromocion1);

        // When
        Promocion result = promocionService.guardarPromocion(testPromocion1);

        // Then
        assertNotNull(result);
        assertEquals("Promo Actualizada", result.getNombre());
        assertEquals(testProducto1.getId(), result.getProducto().getId());
        verify(productoRepository, times(1)).findById(testProducto1.getId());
        verify(promocionRepository, times(1)).save(testPromocion1);
    }


    @Test
    void guardarPromocion_productoAsociadoNoEncontrado_debeLanzarExcepcion() {
        // Given
        Promocion promoConProductoInexistente = new Promocion();
        promoConProductoInexistente.setNombre("Promo Fallida");
        // Asignamos un producto con un ID que no existirá
        Producto productoNoExistente = new Producto();
        productoNoExistente.setId(999);
        promoConProductoInexistente.setProducto(productoNoExistente);

        when(productoRepository.findById(999)).thenReturn(Optional.empty()); // Producto no encontrado

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            promocionService.guardarPromocion(promoConProductoInexistente);
        });

        assertEquals("El producto con ID 999 no existe.", exception.getMessage());
        verify(productoRepository, times(1)).findById(999);
        verify(promocionRepository, never()).save(any(Promocion.class)); // No debe intentar guardar
    }

    @Test
    void borrarPromocion_debeLlamarDeleteById() {
        // Given
        Integer promoIdToDelete = testPromocion1.getId();
        doNothing().when(promocionRepository).deleteById(promoIdToDelete);

        // When
        promocionService.borrarPromocion(promoIdToDelete);

        // Then
        verify(promocionRepository, times(1)).deleteById(promoIdToDelete);
    }

    @Test
    void guardarPromocionLote_debeGuardarTodasLasPromociones() {
        // Given
        List<Promocion> promosToSave = Arrays.asList(testPromocion1, testPromocion2);
        // Mock saveAll para devolver la misma lista (o una con IDs asignados)
        when(promocionRepository.saveAll(promosToSave)).thenReturn(promosToSave);

        // When
        List<Promocion> result = promocionService.guardarPromocionLote(promosToSave);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(testPromocion1.getNombre(), result.get(0).getNombre());
        assertEquals(testPromocion2.getNombre(), result.get(1).getNombre());
        verify(promocionRepository, times(1)).saveAll(promosToSave);
    }
}