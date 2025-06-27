package com.example.Pricing;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.example.Pricing.model.Promocion;
import com.example.Pricing.model.Producto;
import com.example.Pricing.repository.PromocionRepository;
import com.example.Pricing.repository.ProductoRepository;
import com.example.Pricing.services.PromocionService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean; // ¡Se mantiene MockBean!
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

// Mantenemos @SpringBootTest para que Spring se encargue de la inyección de dependencias
@SpringBootTest
public class PromocionServiceTest {

    // @Autowired para inyectar el servicio bajo test
    @Autowired
    private PromocionService promocionService;

    // @MockBean para crear y reemplazar los repositorios con mocks en el contexto de Spring
    @MockitoBean
    private PromocionRepository promocionRepository;
    @MockitoBean
    private ProductoRepository productoRepository;

    // --- Métodos Helper para crear objetos de prueba ---

    private Producto createTestProducto(Integer id, String nombre) {
        Producto p = new Producto();
        p.setId(id);
        p.setNombre(nombre);
        return p;
    }

    private Promocion createTestPromocion(Integer id, String nombre, LocalDateTime fechaInicio, LocalDateTime fechaFin, double descuento, boolean activa, Integer productoId) {
        Promocion promo = new Promocion();
        promo.setId(id);
        promo.setNombre(nombre);
        promo.setFechaInicio(fechaInicio);
        promo.setFechaFin(fechaFin);
        promo.setDescuento(descuento);
        promo.setActiva(activa);
        if (productoId != null) {
            promo.setProducto(createTestProducto(productoId, "Producto " + productoId));
        }
        return promo;
    }



    @Test
    void testFindAll() {
        Promocion promo1 = createTestPromocion(1, "Promo A", LocalDateTime.now(), LocalDateTime.now().plusDays(7), 10.0, true, 101);
        Promocion promo2 = createTestPromocion(2, "Promo B", LocalDateTime.now(), LocalDateTime.now().plusDays(14), 15.0, false, 102);
        List<Promocion> expectedPromotions = Arrays.asList(promo1, promo2);

        when(promocionRepository.findAll()).thenReturn(expectedPromotions);

        
        List<Promocion> foundPromotions = promocionService.findAll();

        
        assertNotNull(foundPromotions);
        assertEquals(2, foundPromotions.size());
        assertEquals(expectedPromotions, foundPromotions);
        verify(promocionRepository, times(1)).findAll();
    }

    @Test
    void testObtenerPromoPorId_found() {
        
        Integer promoId = 1;
        Promocion expectedPromo = createTestPromocion(promoId, "Promo Unica", LocalDateTime.now(), LocalDateTime.now().plusDays(5), 20.0, true, 103);

        when(promocionRepository.findById(promoId)).thenReturn(Optional.of(expectedPromo));

        
        Promocion foundPromo = promocionService.ObtenerPromoPorId(promoId);

        
        assertNotNull(foundPromo);
        assertEquals(promoId, foundPromo.getId());
        assertEquals("Promo Unica", foundPromo.getNombre());
        verify(promocionRepository, times(1)).findById(promoId);
    }

    @Test
    void testObtenerPromoPorId_notFound() {
        
        Integer promoId = 99; 
        when(promocionRepository.findById(promoId)).thenReturn(Optional.empty());

    
        assertThrows(java.util.NoSuchElementException.class, () -> promocionService.ObtenerPromoPorId(promoId));
        verify(promocionRepository, times(1)).findById(promoId);
    }

    @Test
    void testObtenerPromocionesPorIdProducto() {
        
        Integer productId = 101;
        Promocion promo1 = createTestPromocion(1, "Promo Prod A", LocalDateTime.now(), LocalDateTime.now().plusDays(7), 10.0, true, productId);
        Promocion promo2 = createTestPromocion(3, "Promo Prod C", LocalDateTime.now(), LocalDateTime.now().plusDays(10), 5.0, true, productId);
        List<Promocion> expectedPromotions = Arrays.asList(promo1, promo2);

        when(promocionRepository.buscarPromocionesPorProductoId(productId)).thenReturn(expectedPromotions);

        
        List<Promocion> foundPromotions = promocionService.obtenerPromocionesPorIdProducto(productId);

        
        assertNotNull(foundPromotions);
        assertEquals(2, foundPromotions.size());
        assertEquals(expectedPromotions, foundPromotions);
        verify(promocionRepository, times(1)).buscarPromocionesPorProductoId(productId);
    }

    @Test
    void testObtenerPromosActivas() {
        
        Promocion promoActiva1 = createTestPromocion(1, "Activa X", LocalDateTime.now(), LocalDateTime.now().plusDays(7), 10.0, true, 101);
        Promocion promoActiva2 = createTestPromocion(2, "Activa Y", LocalDateTime.now(), LocalDateTime.now().plusDays(14), 15.0, true, 102);
        List<Promocion> expectedActivePromotions = Arrays.asList(promoActiva1, promoActiva2);

        when(promocionRepository.buscaPromoActiva(true)).thenReturn(expectedActivePromotions);

        
        List<Promocion> activePromotions = promocionService.obtenerPromosActivas(true);

        
        assertNotNull(activePromotions);
        assertEquals(2, activePromotions.size());
        assertTrue(activePromotions.get(0).isActiva());
        verify(promocionRepository, times(1)).buscaPromoActiva(true);
    }

    @Test
    void testObtenerPromosPorFechaFin() {
        
        LocalDateTime fechaFin = LocalDateTime.of(2025, 12, 31, 23, 59, 59);
        Promocion promo1 = createTestPromocion(1, "Fin Año", LocalDateTime.now(), fechaFin, 25.0, true, 101);
        List<Promocion> expectedPromotions = Collections.singletonList(promo1);

        when(promocionRepository.buscaPorFechaFin(fechaFin)).thenReturn(expectedPromotions);

        
        List<Promocion> foundPromotions = promocionService.obtenerPromosPorFechaFin(fechaFin);

        
        assertNotNull(foundPromotions);
        assertEquals(1, foundPromotions.size());
        assertEquals(expectedPromotions.get(0).getNombre(), foundPromotions.get(0).getNombre());
        verify(promocionRepository, times(1)).buscaPorFechaFin(fechaFin);
    }

    @Test
    void testObtenerPromosPorNombre() {
        
        String promoName = "Black Friday";
        Promocion promo1 = createTestPromocion(1, "Black Friday Sale", LocalDateTime.now(), LocalDateTime.now().plusDays(3), 30.0, true, 101);
        List<Promocion> expectedPromotions = Collections.singletonList(promo1);

        when(promocionRepository.buscaPorNombre(promoName)).thenReturn(expectedPromotions);

        
        List<Promocion> foundPromotions = promocionService.obtenerPromosPorNombre(promoName);

        
        assertNotNull(foundPromotions);
        assertEquals(1, foundPromotions.size());
        assertEquals(expectedPromotions.get(0).getNombre(), foundPromotions.get(0).getNombre());
        verify(promocionRepository, times(1)).buscaPorNombre(promoName);
    }

    @Test
    void testGuardarPromocion_newPromoWithExistingProduct() {
        
        Producto existingProduct = createTestProducto(101, "Laptop");
        Promocion newPromo = createTestPromocion(null, "Navidad", LocalDateTime.now(), LocalDateTime.now().plusMonths(1), 10.0, true, 101);
        
        
        when(productoRepository.findById(101)).thenReturn(Optional.of(existingProduct));
        
        Promocion savedPromo = createTestPromocion(5, "Navidad", newPromo.getFechaInicio(), newPromo.getFechaFin(), 10.0, true, 101);
        when(promocionRepository.save(any(Promocion.class))).thenReturn(savedPromo);

        
        Promocion result = promocionService.guardarPromocion(newPromo);

        
        assertNotNull(result);
        assertNotNull(result.getId());
        assertEquals(5, result.getId());
        assertEquals("Navidad", result.getNombre());
        assertEquals(existingProduct.getId(), result.getProducto().getId());
        verify(productoRepository, times(1)).findById(101);
        verify(promocionRepository, times(1)).save(any(Promocion.class));
    }

    @Test
    void testGuardarPromocion_newPromoWithoutProduct() {
        
        Promocion newPromo = createTestPromocion(null, "Sin Producto", LocalDateTime.now(), LocalDateTime.now().plusWeeks(2), 5.0, true, null);
        Promocion savedPromo = createTestPromocion(6, "Sin Producto", newPromo.getFechaInicio(), newPromo.getFechaFin(), 5.0, true, null);
        when(promocionRepository.save(any(Promocion.class))).thenReturn(savedPromo);

        
        Promocion result = promocionService.guardarPromocion(newPromo);

        
        assertNotNull(result);
        assertNotNull(result.getId());
        assertNull(result.getProducto());
        verify(productoRepository, never()).findById(any());
        verify(promocionRepository, times(1)).save(any(Promocion.class));
    }

    @Test
    void testGuardarPromocion_existingPromoUpdate() {
        
        Producto existingProduct = createTestProducto(102, "Monitor");
        Promocion existingPromo = createTestPromocion(7, "Update Promo", LocalDateTime.now(), LocalDateTime.now().plusDays(30), 20.0, true, 102);
        existingPromo.setNombre("Updated Name"); // Simulamos un cambio

        when(productoRepository.findById(102)).thenReturn(Optional.of(existingProduct));
        when(promocionRepository.save(any(Promocion.class))).thenReturn(existingPromo);

        
        Promocion result = promocionService.guardarPromocion(existingPromo);

        
        assertNotNull(result);
        assertEquals(7, result.getId());
        assertEquals("Updated Name", result.getNombre());
        verify(productoRepository, times(1)).findById(102);
        verify(promocionRepository, times(1)).save(any(Promocion.class));
    }

    @Test
    void testGuardarPromocion_productNotFoundThrowsException() {
    
        Promocion promoWithNonExistentProduct = createTestPromocion(null, "Error Promo", LocalDateTime.now(), LocalDateTime.now().plusDays(10), 10.0, true, 999);

        when(productoRepository.findById(999)).thenReturn(Optional.empty());

        
        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            promocionService.guardarPromocion(promoWithNonExistentProduct);
        });

        assertEquals("El producto con ID 999 no existe.", thrown.getMessage());
        verify(productoRepository, times(1)).findById(999);
        verify(promocionRepository, never()).save(any(Promocion.class));
    }

    @Test
    void testBorrarPromocion() {
        
        Integer promoIdToDelete = 1;
        doNothing().when(promocionRepository).deleteById(promoIdToDelete);

    
        promocionService.borrarPromocion(promoIdToDelete);

        
        verify(promocionRepository, times(1)).deleteById(promoIdToDelete);
    }

    @Test
    void testGuardarPromocionLote() {

        Promocion promo1 = createTestPromocion(null, "Lote 1", LocalDateTime.now(), LocalDateTime.now().plusDays(5), 5.0, true, 201);
        Promocion promo2 = createTestPromocion(null, "Lote 2", LocalDateTime.now(), LocalDateTime.now().plusDays(10), 8.0, true, 202);
        List<Promocion> promosToSave = Arrays.asList(promo1, promo2);

        Promocion savedPromo1 = createTestPromocion(10, "Lote 1", promo1.getFechaInicio(), promo1.getFechaFin(), 5.0, true, 201);
        Promocion savedPromo2 = createTestPromocion(11, "Lote 2", promo2.getFechaInicio(), promo2.getFechaFin(), 8.0, true, 202);
        List<Promocion> savedPromos = Arrays.asList(savedPromo1, savedPromo2);

        when(promocionRepository.saveAll(promosToSave)).thenReturn(savedPromos);


        List<Promocion> result = promocionService.guardarPromocionLote(promosToSave);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(savedPromos.get(0).getId(), result.get(0).getId());
        assertEquals(savedPromos.get(1).getNombre(), result.get(1).getNombre());
        verify(promocionRepository, times(1)).saveAll(promosToSave);
    }
}