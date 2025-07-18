package com.example.Pricing; // Este package debería ser com.example.Pricing.controller

import com.example.Pricing.assemblers.PricingAssembler;
import com.example.Pricing.controller.PricingController;
import com.example.Pricing.model.PrecioBase;
import com.example.Pricing.model.Promocion;
import com.example.Pricing.model.Variante;
import com.example.Pricing.repository.PrecioBaseRepository;
import com.example.Pricing.repository.PromocionRepository;
import com.example.Pricing.repository.VarianteRepository;
import com.example.Pricing.services.PrecioBaseService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import java.util.Collections; // Importar para lista vacía
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

// Asegúrate de que este sea el paquete correcto para los tests
// package com.example.Pricing.controller; // Sugerencia de paquete para los tests

@ExtendWith(MockitoExtension.class)
class PricingControllerTest {

    @InjectMocks
    private PricingController pricingController;

    @Mock
    private PrecioBaseRepository precioBaseRepository;

    @Mock
    private PrecioBaseService precioBaseService; // Este mock solo es usado en crearPreciosBaseLote. Es correcto.

    @Mock
    private PromocionRepository promocionRepository;

    @Mock
    private VarianteRepository varianteRepository;

    @Mock
    private PricingAssembler pricingAssembler;

    private PrecioBase precioBaseActivo;
    private PrecioBase precioBaseInactivo; // Añadimos para más escenarios
    private Promocion promocionActiva;
    private Variante varianteExistente;

    @BeforeEach
    void setUp() {
        precioBaseActivo = new PrecioBase();
        precioBaseActivo.setId(1);
        precioBaseActivo.setMonto(1000.0);
        precioBaseActivo.setActivo(true); // Default true

        precioBaseInactivo = new PrecioBase();
        precioBaseInactivo.setId(2);
        precioBaseInactivo.setMonto(500.0);
        precioBaseInactivo.setActivo(false); // Explicitly false

        promocionActiva = new Promocion();
        promocionActiva.setId(1); // Asocia a precioBaseActivo
        promocionActiva.setPorcentajeDescuento(10.0);
        promocionActiva.setActivo(true);

        varianteExistente = new Variante();
        varianteExistente.setId(101); // ID de variante
        varianteExistente.setValor(200.0); // Valor que añade al precio
    }

    // --- obtenerPrecios ---
    @Test
    void obtenerPrecios_debeRetornarListaDePreciosBaseActivos() {
        // Given
        // Retornamos ambos, el assembler solo trabajará con los activos si el filtro es del controller
        // (En tu controlador actual, findAll no filtra por activo, pero el assembler construye enlaces para activar/desactivar)
        when(precioBaseRepository.findAll()).thenReturn(List.of(precioBaseActivo, precioBaseInactivo));
        when(pricingAssembler.toModel(any(PrecioBase.class))).thenAnswer(invocation -> {
            PrecioBase pb = invocation.getArgument(0);
            return EntityModel.of(pb);
        });

        // When
        CollectionModel<EntityModel<PrecioBase>> response = pricingController.obtenerPrecios();
        List<EntityModel<PrecioBase>> precios = response.getContent().stream().collect(Collectors.toList());

        // Then
        assertNotNull(precios);
        assertEquals(2, precios.size()); // Esperamos dos porque findAll no filtra
        verify(precioBaseRepository, times(1)).findAll();
        verify(pricingAssembler, times(2)).toModel(any(PrecioBase.class)); // Verificamos que se llamó para ambos
    }

    @Test
    void obtenerPrecios_cuandoNoHayPrecios_debeRetornarListaVacia() {
        // Given
        when(precioBaseRepository.findAll()).thenReturn(Collections.emptyList());

        // When
        CollectionModel<EntityModel<PrecioBase>> response = pricingController.obtenerPrecios();
        List<EntityModel<PrecioBase>> precios = response.getContent().stream().collect(Collectors.toList());

        // Then
        assertNotNull(precios);
        assertTrue(precios.isEmpty());
        verify(precioBaseRepository, times(1)).findAll();
        verify(pricingAssembler, never()).toModel(any(PrecioBase.class)); // No se debe llamar al assembler si no hay precios
    }


    // --- obtenerPrecioPorId ---
    @Test
    void obtenerPrecioPorId_existente_debeRetornarPrecioYOK() {
        // Given
        when(precioBaseRepository.findById(precioBaseActivo.getId())).thenReturn(Optional.of(precioBaseActivo));
        when(pricingAssembler.toModel(precioBaseActivo)).thenReturn(EntityModel.of(precioBaseActivo));

        // When
        ResponseEntity<EntityModel<PrecioBase>> response = pricingController.obtenerPrecioPorId(precioBaseActivo.getId());

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(precioBaseActivo, response.getBody().getContent());
        verify(precioBaseRepository, times(1)).findById(precioBaseActivo.getId());
        verify(pricingAssembler, times(1)).toModel(precioBaseActivo);
    }

    @Test
    void obtenerPrecioPorId_noExistente_debeRetornarNotFound() {
        // Given
        when(precioBaseRepository.findById(999)).thenReturn(Optional.empty());

        // When
        ResponseEntity<EntityModel<PrecioBase>> response = pricingController.obtenerPrecioPorId(999);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
        verify(precioBaseRepository, times(1)).findById(999);
        verify(pricingAssembler, never()).toModel(any(PrecioBase.class)); // No se debe llamar al assembler
    }


    // --- activarPrecio ---
    @Test
    void activarPrecio_existente_yAhoraActivo_debeRetornarOK() {
        // Given
        precioBaseInactivo.setActivo(false); // Aseguramos que está inactivo para la prueba
        when(precioBaseRepository.findById(precioBaseInactivo.getId())).thenReturn(Optional.of(precioBaseInactivo));
        when(precioBaseRepository.save(any(PrecioBase.class))).thenAnswer(invocation -> {
            PrecioBase savedPrecio = invocation.getArgument(0);
            savedPrecio.setActivo(true); // Simula que el save lo activa
            return savedPrecio;
        });
        when(pricingAssembler.toModel(any(PrecioBase.class))).thenReturn(EntityModel.of(precioBaseInactivo)); // Ya está activo después del save

        // When
        ResponseEntity<EntityModel<PrecioBase>> response = pricingController.activarPrecio(precioBaseInactivo.getId());

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().getContent().isActivo()); // Debe ser true
        verify(precioBaseRepository, times(1)).findById(precioBaseInactivo.getId());
        verify(precioBaseRepository, times(1)).save(precioBaseInactivo); // Verifica que se guardó el objeto modificado
        verify(pricingAssembler, times(1)).toModel(precioBaseInactivo);
    }

    @Test
    void activarPrecio_noExistente_debeRetornarNotFound() {
        // Given
        when(precioBaseRepository.findById(999)).thenReturn(Optional.empty());

        // When
        ResponseEntity<EntityModel<PrecioBase>> response = pricingController.activarPrecio(999);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
        verify(precioBaseRepository, times(1)).findById(999);
        verify(precioBaseRepository, never()).save(any(PrecioBase.class));
        verify(pricingAssembler, never()).toModel(any(PrecioBase.class));
    }


    // --- desactivarPrecio ---
    @Test
    void desactivarPrecio_existente_yAhoraInactivo_debeRetornarOK() {
        // Given
        precioBaseActivo.setActivo(true); // Aseguramos que está activo para la prueba
        when(precioBaseRepository.findById(precioBaseActivo.getId())).thenReturn(Optional.of(precioBaseActivo));
        when(precioBaseRepository.save(any(PrecioBase.class))).thenAnswer(invocation -> {
            PrecioBase savedPrecio = invocation.getArgument(0);
            savedPrecio.setActivo(false); // Simula que el save lo desactiva
            return savedPrecio;
        });
        when(pricingAssembler.toModel(any(PrecioBase.class))).thenReturn(EntityModel.of(precioBaseActivo)); // Ya está inactivo después del save

        // When
        ResponseEntity<EntityModel<PrecioBase>> response = pricingController.desactivarPrecio(precioBaseActivo.getId());

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().getContent().isActivo()); // Debe ser false
        verify(precioBaseRepository, times(1)).findById(precioBaseActivo.getId());
        verify(precioBaseRepository, times(1)).save(precioBaseActivo);
        verify(pricingAssembler, times(1)).toModel(precioBaseActivo);
    }

    @Test
    void desactivarPrecio_noExistente_debeRetornarNotFound() {
        // Given
        when(precioBaseRepository.findById(999)).thenReturn(Optional.empty());

        // When
        ResponseEntity<EntityModel<PrecioBase>> response = pricingController.desactivarPrecio(999);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
        verify(precioBaseRepository, times(1)).findById(999);
        verify(precioBaseRepository, never()).save(any(PrecioBase.class));
        verify(pricingAssembler, never()).toModel(any(PrecioBase.class));
    }


    // --- crearPrecioBase ---
    @Test
    void crearPrecioBase_debeCrearYRetornarPrecioConCreated() {
        // Given
        // El precioBaseActivo tiene un ID, lo cual es normal para un objeto que se va a guardar y se le asignará uno.
        // Simulamos que al guardar, el repositorio devuelve el mismo objeto.
        when(precioBaseRepository.save(any(PrecioBase.class))).thenReturn(precioBaseActivo);
        when(pricingAssembler.toModel(precioBaseActivo)).thenReturn(EntityModel.of(precioBaseActivo));

        // When
        ResponseEntity<EntityModel<PrecioBase>> response = pricingController.crearPrecioBase(precioBaseActivo);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(precioBaseActivo, response.getBody().getContent());
        verify(precioBaseRepository, times(1)).save(precioBaseActivo);
        verify(pricingAssembler, times(1)).toModel(precioBaseActivo);
    }


    // --- crearPreciosBaseLote ---
    @Test
    void crearPreciosBaseLote_conListaValida_debeCrearYRetornarCreated() {
        // Given
        List<PrecioBase> preciosParaGuardar = List.of(precioBaseActivo, precioBaseInactivo);
        when(precioBaseService.guardarPreciosBase(anyList())).thenReturn(preciosParaGuardar);
        when(pricingAssembler.toModel(any(PrecioBase.class))).thenAnswer(invocation -> {
            PrecioBase pb = invocation.getArgument(0);
            return EntityModel.of(pb);
        });

        // When
        ResponseEntity<CollectionModel<EntityModel<PrecioBase>>> response = pricingController.crearPreciosBaseLote(preciosParaGuardar);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().getContent().size());
        verify(precioBaseService, times(1)).guardarPreciosBase(preciosParaGuardar);
        verify(pricingAssembler, times(2)).toModel(any(PrecioBase.class));
    }

    @Test
    void crearPreciosBaseLote_conListaVacia_debeRetornarBadRequest() {
        // When
        ResponseEntity<CollectionModel<EntityModel<PrecioBase>>> response = pricingController.crearPreciosBaseLote(Collections.emptyList());

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNull(response.getBody()); // Body should be null for BAD_REQUEST
        verify(precioBaseService, never()).guardarPreciosBase(anyList());
        verify(pricingAssembler, never()).toModel(any(PrecioBase.class));
    }

    @Test
    void crearPreciosBaseLote_conListaNula_debeRetornarBadRequest() {
        // When
        ResponseEntity<CollectionModel<EntityModel<PrecioBase>>> response = pricingController.crearPreciosBaseLote(null);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNull(response.getBody()); // Body should be null for BAD_REQUEST
        verify(precioBaseService, never()).guardarPreciosBase(anyList());
        verify(pricingAssembler, never()).toModel(any(PrecioBase.class));
    }


    // --- obtenerPrecioFinal ---
    @Test
    void obtenerPrecioFinal_sinVariante_sinPromocion_debeRetornarPrecioBase() {
        // Given
        when(precioBaseRepository.findById(precioBaseActivo.getId())).thenReturn(Optional.of(precioBaseActivo));
        when(promocionRepository.findByProductoIdAndActivoTrue(precioBaseActivo.getId())).thenReturn(Optional.empty());
        // Variante ID es Optional.empty()

        // When
        ResponseEntity<Double> response = pricingController.obtenerPrecioFinal(precioBaseActivo.getId(), Optional.empty());

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(precioBaseActivo.getMonto(), response.getBody());
        verify(precioBaseRepository, times(1)).findById(precioBaseActivo.getId());
        verify(promocionRepository, times(1)).findByProductoIdAndActivoTrue(precioBaseActivo.getId());
        verify(varianteRepository, never()).findById(anyInt()); // No debe buscar variante
    }

    @Test
    void obtenerPrecioFinal_conVariante_sinPromocion_debeRetornarPrecioMasVariante() {
        // Given
        when(precioBaseRepository.findById(precioBaseActivo.getId())).thenReturn(Optional.of(precioBaseActivo));
        when(varianteRepository.findById(varianteExistente.getId())).thenReturn(Optional.of(varianteExistente));
        when(promocionRepository.findByProductoIdAndActivoTrue(precioBaseActivo.getId())).thenReturn(Optional.empty());

        // When
        ResponseEntity<Double> response = pricingController.obtenerPrecioFinal(precioBaseActivo.getId(), Optional.of(varianteExistente.getId()));

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(precioBaseActivo.getMonto() + varianteExistente.getValor(), response.getBody()); // 1000 + 200 = 1200
        verify(precioBaseRepository, times(1)).findById(precioBaseActivo.getId());
        verify(varianteRepository, times(1)).findById(varianteExistente.getId());
        verify(promocionRepository, times(1)).findByProductoIdAndActivoTrue(precioBaseActivo.getId());
    }

    @Test
    void obtenerPrecioFinal_sinVariante_conPromocion_debeRetornarPrecioConDescuento() {
        // Given
        when(precioBaseRepository.findById(precioBaseActivo.getId())).thenReturn(Optional.of(precioBaseActivo));
        when(promocionRepository.findByProductoIdAndActivoTrue(precioBaseActivo.getId())).thenReturn(Optional.of(promocionActiva));

        // When
        ResponseEntity<Double> response = pricingController.obtenerPrecioFinal(precioBaseActivo.getId(), Optional.empty());

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        // (1000 * (1 - 0.10)) = 900.0
        assertEquals(precioBaseActivo.getMonto() * (1 - promocionActiva.getPorcentajeDescuento() / 100), response.getBody());
        verify(precioBaseRepository, times(1)).findById(precioBaseActivo.getId());
        verify(varianteRepository, never()).findById(anyInt()); // No debe buscar variante
        verify(promocionRepository, times(1)).findByProductoIdAndActivoTrue(precioBaseActivo.getId());
    }

    @Test
    void obtenerPrecioFinal_conVariante_conPromocion_debeRetornarPrecioFinalConDescuento() {
        // Given
        when(precioBaseRepository.findById(precioBaseActivo.getId())).thenReturn(Optional.of(precioBaseActivo));
        when(varianteRepository.findById(varianteExistente.getId())).thenReturn(Optional.of(varianteExistente));
        when(promocionRepository.findByProductoIdAndActivoTrue(precioBaseActivo.getId())).thenReturn(Optional.of(promocionActiva));

        // When
        ResponseEntity<Double> response = pricingController.obtenerPrecioFinal(precioBaseActivo.getId(), Optional.of(varianteExistente.getId()));

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        // (1000 + 200) * (1 - 0.10) = 1200 * 0.9 = 1080.0
        double expectedPrice = (precioBaseActivo.getMonto() + varianteExistente.getValor()) * (1 - promocionActiva.getPorcentajeDescuento() / 100);
        assertEquals(expectedPrice, response.getBody());
        verify(precioBaseRepository, times(1)).findById(precioBaseActivo.getId());
        verify(varianteRepository, times(1)).findById(varianteExistente.getId());
        verify(promocionRepository, times(1)).findByProductoIdAndActivoTrue(precioBaseActivo.getId());
    }

    @Test
    void obtenerPrecioFinal_precioNoEncontrado_debeLanzarExcepcion() {
        // Given
        when(precioBaseRepository.findById(anyInt())).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            pricingController.obtenerPrecioFinal(999, Optional.empty());
        });

        assertEquals("Precio no encontrado para el producto con ID: 999", exception.getMessage());
        verify(precioBaseRepository, times(1)).findById(999);
        verify(varianteRepository, never()).findById(anyInt());
        verify(promocionRepository, never()).findByProductoIdAndActivoTrue(anyInt());
    }

    @Test
    void obtenerPrecioFinal_varianteIdPresentePeroVarianteNoEncontrada_debeLanzarExcepcion() {
        // Given
        when(precioBaseRepository.findById(precioBaseActivo.getId())).thenReturn(Optional.of(precioBaseActivo));
        when(varianteRepository.findById(999)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            pricingController.obtenerPrecioFinal(precioBaseActivo.getId(), Optional.of(999));
        });

        assertEquals("Variante no encontrada con ID: 999", exception.getMessage());
        verify(precioBaseRepository, times(1)).findById(precioBaseActivo.getId());
        verify(varianteRepository, times(1)).findById(999);
        verify(promocionRepository, never()).findByProductoIdAndActivoTrue(anyInt());
    }
}