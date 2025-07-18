package com.example.Pricing;


import com.example.Pricing.model.Variante;
import com.example.Pricing.model.Producto; // Asumo que Variante tiene un campo Producto
import com.example.Pricing.repository.VarianteRepository;
import com.example.Pricing.services.VarianteService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException; // Importar para la excepción Optional.get()
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VarianteServiceTest {

    @InjectMocks
    private VarianteService varianteService;

    @Mock
    private VarianteRepository varianteRepository;

    private Variante variante1;
    private Variante variante2;
    private Producto productoAsociado;

    @BeforeEach
    void setUp() {
        productoAsociado = new Producto();
        productoAsociado.setId(100);
        productoAsociado.setNombre("Producto de prueba");

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
    void findAll_debeRetornarTodasLasVariantes() {
        // Given
        when(varianteRepository.findAll()).thenReturn(Arrays.asList(variante1, variante2));

        // When
        List<Variante> result = varianteService.findAll();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains(variante1));
        assertTrue(result.contains(variante2));
        verify(varianteRepository, times(1)).findAll();
    }

    @Test
    void ObtenerProVariantePorId_existente_debeRetornarVariante() {
        // Given
        when(varianteRepository.findById(1)).thenReturn(Optional.of(variante1));

        // When
        Variante result = varianteService.ObtenerProVariantePorId(1);

        // Then
        assertNotNull(result);
        assertEquals(variante1.getId(), result.getId());
        assertEquals(variante1.getTipo(), result.getTipo());
        verify(varianteRepository, times(1)).findById(1);
    }

    @Test
    void ObtenerProVariantePorId_noExistente_debeLanzarNoSuchElementException() {
        // Given
        when(varianteRepository.findById(99)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(NoSuchElementException.class, () -> {
            varianteService.ObtenerProVariantePorId(99);
        });
        verify(varianteRepository, times(1)).findById(99);
    }

    @Test
    void guardarVariante_debeGuardarYRetornarVariante() {
        // Given
        when(varianteRepository.save(any(Variante.class))).thenReturn(variante1);

        // When
        Variante result = varianteService.guardarVariante(variante1);

        // Then
        assertNotNull(result);
        assertEquals(variante1.getTipo(), result.getTipo());
        verify(varianteRepository, times(1)).save(variante1);
    }

    @Test
    void borrarVariante_debeLlamarDeleteById() {
        // Given
        Integer idABorrar = 1;
        doNothing().when(varianteRepository).deleteById(idABorrar); // Configura el mock para métodos void

        // When
        varianteService.borrarVariante(idABorrar);

        // Then
        verify(varianteRepository, times(1)).deleteById(idABorrar);
    }

    @Test
    void guardarVariantes_debeGuardarYRetornarListaDeVariantes() {
        // Given
        List<Variante> variantesAGuardar = Arrays.asList(variante1, variante2);
        when(varianteRepository.saveAll(anyList())).thenReturn(variantesAGuardar);

        // When
        List<Variante> result = varianteService.guardarVariantes(variantesAGuardar);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains(variante1));
        assertTrue(result.contains(variante2));
        verify(varianteRepository, times(1)).saveAll(variantesAGuardar);
    }
}