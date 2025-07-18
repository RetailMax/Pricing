package com.example.Pricing;



import com.example.Pricing.model.Variante;
import com.example.Pricing.controller.VarianteController;
import com.example.Pricing.model.Producto; // Asumo que Variante tiene un campo Producto
import com.example.Pricing.services.VarianteService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VarianteControllerTest {

    @InjectMocks
    private VarianteController varianteController;

    @Mock
    private VarianteService varianteService;

    private Variante variante1;
    private Variante variante2;
    private Producto productoAsociado;

    @BeforeEach
    void setUp() {
        productoAsociado = new Producto();
        productoAsociado.setId(100);
        productoAsociado.setNombre("Producto X");

        variante1 = new Variante();
        variante1.setId(1);
        variante1.setTipo("Color");
        variante1.setValor(50.0);
        variante1.setProducto(productoAsociado);

        variante2 = new Variante();
        variante2.setId(2);
        variante2.setTipo("Talla");
        variante2.setValor(20.0);
        variante2.setProducto(productoAsociado);
    }

    @Test
    void obtenerVariantes_conVariantesExistentes_debeRetornarListaYOK() {
        // Given
        when(varianteService.findAll()).thenReturn(Arrays.asList(variante1, variante2));

        // When
        ResponseEntity<List<Variante>> response = varianteController.obtenerVariantes();

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
        assertTrue(response.getBody().contains(variante1));
        assertTrue(response.getBody().contains(variante2));
        verify(varianteService, times(1)).findAll();
    }

    @Test
    void obtenerVariantes_sinVariantes_debeRetornarNoContent() {
        // Given
        when(varianteService.findAll()).thenReturn(Collections.emptyList());

        // When
        ResponseEntity<List<Variante>> response = varianteController.obtenerVariantes();

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody()); // Body should be null for NO_CONTENT
        verify(varianteService, times(1)).findAll();
    }

    @Test
    void obtenerVariantePorId_existente_debeRetornarVarianteYOK() {
        // Given
        when(varianteService.ObtenerProVariantePorId(1)).thenReturn(variante1);

        // When
        ResponseEntity<Variante> response = varianteController.obtenerVariantePorId(1);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(variante1.getId(), response.getBody().getId());
        assertEquals(variante1.getTipo(), response.getBody().getTipo());
        verify(varianteService, times(1)).ObtenerProVariantePorId(1);
    }

    @Test
    void obtenerVariantePorId_noExistente_debeRetornarNotFound() {
        // Given
        when(varianteService.ObtenerProVariantePorId(99)).thenReturn(null);

        // When
        ResponseEntity<Variante> response = varianteController.obtenerVariantePorId(99);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
        verify(varianteService, times(1)).ObtenerProVariantePorId(99);
    }

    @Test
    void crearVariante_debeCrearYRetornarVarianteConCreated() {
        // Given
        when(varianteService.guardarVariante(any(Variante.class))).thenReturn(variante1);

        // When
        ResponseEntity<Variante> response = varianteController.crearVariante(variante1);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(variante1, response.getBody());
        verify(varianteService, times(1)).guardarVariante(variante1);
    }

    @Test
    void actualizarVariante_existente_debeActualizarYRetornarOK() {
        // Given
        Variante varianteActualizadaInput = new Variante();
        varianteActualizadaInput.setTipo("Material");
        varianteActualizadaInput.setValor(75.0);
        varianteActualizadaInput.setProducto(productoAsociado); // Asumimos el mismo producto o un nuevo producto mockeado

        // Mock varianteExistente retrieval
        when(varianteService.ObtenerProVariantePorId(1)).thenReturn(variante1);
        // Mock save returning the "updated" variant
        when(varianteService.guardarVariante(any(Variante.class))).thenReturn(varianteActualizadaInput);

        // When
        ResponseEntity<Variante> response = varianteController.actualizarVariante(1, varianteActualizadaInput);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(varianteActualizadaInput.getTipo(), response.getBody().getTipo());
        assertEquals(varianteActualizadaInput.getValor(), response.getBody().getValor());
        assertEquals(varianteActualizadaInput.getProducto(), response.getBody().getProducto());

        // Verify that original variant's fields were updated before saving
        verify(varianteService, times(1)).ObtenerProVariantePorId(1);
        // Ensure that the original 'variante1' object received the updates from 'varianteActualizadaInput'
        assertEquals("Material", variante1.getTipo());
        assertEquals(75.0, variante1.getValor());
        assertEquals(productoAsociado, variante1.getProducto());
        verify(varianteService, times(1)).guardarVariante(variante1); // Verify save was called with the modified original object
    }

    @Test
    void actualizarVariante_noExistente_debeRetornarNotFound() {
        // Given
        Variante varianteActualizada = new Variante();
        varianteActualizada.setTipo("Tamaño");

        when(varianteService.ObtenerProVariantePorId(99)).thenReturn(null); // Variante no encontrada

        // When
        ResponseEntity<Variante> response = varianteController.actualizarVariante(99, varianteActualizada);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
        verify(varianteService, times(1)).ObtenerProVariantePorId(99);
        verify(varianteService, never()).guardarVariante(any(Variante.class)); // Ensure save was never called
    }

    @Test
    void eliminarVariante_existente_debeEliminarYRetornarNoContent() {
        // Given
        when(varianteService.ObtenerProVariantePorId(1)).thenReturn(variante1);
        doNothing().when(varianteService).borrarVariante(1); // For void method

        // When
        ResponseEntity<Void> response = varianteController.eliminarVariante(1);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
        verify(varianteService, times(1)).ObtenerProVariantePorId(1);
        verify(varianteService, times(1)).borrarVariante(1);
    }

    @Test
    void eliminarVariante_noExistente_debeRetornarNotFound() {
        // Given
        when(varianteService.ObtenerProVariantePorId(99)).thenReturn(null);

        // When
        ResponseEntity<Void> response = varianteController.eliminarVariante(99);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
        verify(varianteService, times(1)).ObtenerProVariantePorId(99);
        verify(varianteService, never()).borrarVariante(anyInt()); // Ensure delete was never called
    }

    @Test
    void crearVariantesLote_loteValido_debeCrearYRetornarCreated() {
        // Given
        List<Variante> variantesParaGuardar = Arrays.asList(variante1, variante2);
        when(varianteService.guardarVariantes(anyList())).thenReturn(variantesParaGuardar);

        // When
        ResponseEntity<List<Variante>> response = varianteController.crearVariantesLote(variantesParaGuardar);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
        verify(varianteService, times(1)).guardarVariantes(variantesParaGuardar);
    }

    @Test
    void crearVariantesLote_loteVacio_debeRetornarBadRequest() {
        // When
        ResponseEntity<List<Variante>> response = varianteController.crearVariantesLote(Collections.emptyList());

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNull(response.getBody());
        verify(varianteService, never()).guardarVariantes(anyList());
    }

    @Test
    void crearVariantesLote_loteNulo_debeRetornarBadRequest() {
        // When
        ResponseEntity<List<Variante>> response = varianteController.crearVariantesLote(null);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNull(response.getBody());
        verify(varianteService, never()).guardarVariantes(anyList());
    }
}