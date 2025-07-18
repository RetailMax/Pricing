package com.example.Pricing;

import com.example.Pricing.controller.PromocionController;
import com.example.Pricing.model.Promocion;
import com.example.Pricing.repository.PromocionRepository;
import com.example.Pricing.services.PromocionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PromocionControllerTest {

    @InjectMocks
    private PromocionController promocionController;

    @Mock
    private PromocionRepository promocionRepository;

    @Mock
    private PromocionService promocionService;

    private Promocion promocionActiva;
    private Promocion promocionInactiva;

    @BeforeEach
    void setUp() {
        promocionActiva = new Promocion();
        promocionActiva.setId(1);
        promocionActiva.setNombre("Promo Verano");
        promocionActiva.setPorcentajeDescuento(15.0);
        promocionActiva.setActivo(true);

        promocionInactiva = new Promocion();
        promocionInactiva.setId(2);
        promocionInactiva.setNombre("Promo Invierno");
        promocionInactiva.setPorcentajeDescuento(10.0);
        promocionInactiva.setActivo(false);
    }

    @Test
    void obtenerPromociones_debeRetornarListaDePromociones() {
        when(promocionRepository.findAll()).thenReturn(Arrays.asList(promocionActiva, promocionInactiva));

        List<Promocion> promociones = promocionController.obtenerPromociones();

        assertNotNull(promociones);
        assertEquals(2, promociones.size());
        assertTrue(promociones.contains(promocionActiva));
        assertTrue(promociones.contains(promocionInactiva));
        verify(promocionRepository, times(1)).findAll();
    }

    @Test
    void crearPromocion_debeCrearYRetornarPromocionConEstadoCreated() {
        when(promocionRepository.save(any(Promocion.class))).thenReturn(promocionActiva);

        ResponseEntity<Promocion> response = promocionController.crearPromocion(promocionActiva);

        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(promocionActiva, response.getBody());
        verify(promocionRepository, times(1)).save(promocionActiva);
    }

    @Test
    void obtenerPromoPorId_existente_debeRetornarPromocion() {
        when(promocionRepository.findById(1)).thenReturn(Optional.of(promocionActiva));

        Promocion foundPromocion = promocionController.obtenerPromoPorId(1);

        assertNotNull(foundPromocion);
        assertEquals(promocionActiva.getId(), foundPromocion.getId());
        assertEquals(promocionActiva.getNombre(), foundPromocion.getNombre());
        verify(promocionRepository, times(1)).findById(1);
    }

    @Test
    void obtenerPromoPorId_noExistente_debeRetornarNull() {
        when(promocionRepository.findById(99)).thenReturn(Optional.empty());

        Promocion foundPromocion = promocionController.obtenerPromoPorId(99);

        assertNull(foundPromocion);
        verify(promocionRepository, times(1)).findById(99);
    }

    @Test
    void actualizarEstado_deActivoAInactivo_debeActualizarEstadoYRetornarOk() {
        // Mock the initial state (activo = true)
        when(promocionRepository.findById(1)).thenReturn(Optional.of(promocionActiva));
        // Mock the save operation after changing state
        when(promocionRepository.save(any(Promocion.class))).thenReturn(promocionInactiva); // Returns a promo that is now inactive

        ResponseEntity<String> response = promocionController.actualizarEstado(1);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Estado actualizado a false", response.getBody()); // Check the message matches new state
        assertFalse(promocionActiva.isActivo()); // Verify the original object's state changed
        verify(promocionRepository, times(1)).findById(1);
        verify(promocionRepository, times(1)).save(promocionActiva); // Verify save was called
    }

    @Test
    void actualizarEstado_deInactivoAActivo_debeActualizarEstadoYRetornarOk() {
        // Mock the initial state (activo = false)
        when(promocionRepository.findById(2)).thenReturn(Optional.of(promocionInactiva));
        // Mock the save operation after changing state
        when(promocionRepository.save(any(Promocion.class))).thenReturn(promocionActiva); // Returns a promo that is now active

        ResponseEntity<String> response = promocionController.actualizarEstado(2);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Estado actualizado a true", response.getBody()); // Check the message matches new state
        assertTrue(promocionInactiva.isActivo()); // Verify the original object's state changed
        verify(promocionRepository, times(1)).findById(2);
        verify(promocionRepository, times(1)).save(promocionInactiva); // Verify save was called
    }


    @Test
    void actualizarEstado_noExistente_debeRetornarNotFound() {
        when(promocionRepository.findById(99)).thenReturn(Optional.empty());

        ResponseEntity<String> response = promocionController.actualizarEstado(99);

        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(promocionRepository, times(1)).findById(99);
        verify(promocionRepository, never()).save(any(Promocion.class)); // Ensure save was not called
    }

    @Test
    void crearPromocionLote_conListaValida_debeCrearYRetornarListaConEstadoCreated() {
        List<Promocion> promocionesInput = Arrays.asList(promocionActiva, promocionInactiva);
        when(promocionService.guardarPromocionLote(anyList())).thenReturn(promocionesInput);

        ResponseEntity<List<Promocion>> response = promocionController.crearPromocionLote(promocionesInput);

        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(2, response.getBody().size());
        verify(promocionService, times(1)).guardarPromocionLote(promocionesInput);
    }

    @Test
    void crearPromocionLote_conListaVacia_debeRetornarBadRequest() {
        ResponseEntity<List<Promocion>> response = promocionController.crearPromocionLote(Arrays.asList());

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(promocionService, never()).guardarPromocionLote(anyList()); // Ensure service method was not called
    }

    @Test
    void crearPromocionLote_conListaNula_debeRetornarBadRequest() {
        ResponseEntity<List<Promocion>> response = promocionController.crearPromocionLote(null);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(promocionService, never()).guardarPromocionLote(anyList()); // Ensure service method was not called
    }
}