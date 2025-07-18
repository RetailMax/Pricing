package com.example.Pricing;


import com.example.Pricing.controller.ProductoController2;
import com.example.Pricing.model.Producto;
import com.example.Pricing.repository.ProductoRepository;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductoController2Test {

    @InjectMocks
    private ProductoController2 productoController2;

    @Mock
    private ProductoRepository productoRepository;

    private Producto producto1;
    private Producto producto2;

    @BeforeEach
    void setUp() {
        producto1 = new Producto();
        producto1.setId(1);
        producto1.setNombre("Laptop");
        producto1.setDescripcion("Potente laptop para gaming");

        producto2 = new Producto();
        producto2.setId(2);
        producto2.setNombre("Mouse");
        producto2.setDescripcion("Mouse ergonómico inalámbrico");
    }

    @Test
    void obtenerProductos_conProductosExistentes_debeRetornarListaYOK() {
        when(productoRepository.findAll()).thenReturn(Arrays.asList(producto1, producto2));

        ResponseEntity<List<Producto>> response = productoController2.obtenerProductos();

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
        assertTrue(response.getBody().contains(producto1));
        assertTrue(response.getBody().contains(producto2));
        verify(productoRepository, times(1)).findAll();
    }

    @Test
    void obtenerProductos_sinProductos_debeRetornarNoContent() {
        when(productoRepository.findAll()).thenReturn(Collections.emptyList());

        ResponseEntity<List<Producto>> response = productoController2.obtenerProductos();

        assertNotNull(response);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody()); // Body should be null for NO_CONTENT
        verify(productoRepository, times(1)).findAll();
    }

    @Test
    void obtenerProductoPorId_existente_debeRetornarProductoYOK() {
        when(productoRepository.findById(1)).thenReturn(Optional.of(producto1));

        ResponseEntity<Producto> response = productoController2.obtenerProductoPorId(1);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(producto1.getId(), response.getBody().getId());
        assertEquals(producto1.getNombre(), response.getBody().getNombre());
        verify(productoRepository, times(1)).findById(1);
    }

    @Test
    void obtenerProductoPorId_noExistente_debeRetornarNotFound() {
        when(productoRepository.findById(99)).thenReturn(Optional.empty());

        ResponseEntity<Producto> response = productoController2.obtenerProductoPorId(99);

        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
        verify(productoRepository, times(1)).findById(99);
    }

    @Test
    void crearProducto_debeCrearYRetornarProductoConCreated() {
        when(productoRepository.save(any(Producto.class))).thenReturn(producto1);

        ResponseEntity<Producto> response = productoController2.crearProducto(producto1);

        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(producto1, response.getBody());
        verify(productoRepository, times(1)).save(producto1);
    }

    @Test
    void actualizarProducto_existente_debeActualizarYRetornarOK() {
        Producto productoActualizado = new Producto();
        productoActualizado.setNombre("Laptop Pro");
        productoActualizado.setDescripcion("Laptop de alta gama para profesionales");

        when(productoRepository.findById(1)).thenReturn(Optional.of(producto1));
        when(productoRepository.save(any(Producto.class))).thenReturn(productoActualizado); // Mock save returns the updated product

        ResponseEntity<Producto> response = productoController2.actualizarProducto(1, productoActualizado);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(productoActualizado.getNombre(), response.getBody().getNombre());
        assertEquals(productoActualizado.getDescripcion(), response.getBody().getDescripcion());
        // Verify that original product's fields were updated before saving
        verify(productoRepository, times(1)).findById(1);
        verify(productoRepository, times(1)).save(producto1); // Ensure save was called with the modified original product
        assertEquals("Laptop Pro", producto1.getNombre()); // Verify original object was modified
        assertEquals("Laptop de alta gama para profesionales", producto1.getDescripcion());
    }

    @Test
    void actualizarProducto_noExistente_debeRetornarNotFound() {
        Producto productoActualizado = new Producto();
        productoActualizado.setNombre("Laptop Pro");

        when(productoRepository.findById(99)).thenReturn(Optional.empty());

        ResponseEntity<Producto> response = productoController2.actualizarProducto(99, productoActualizado);

        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
        verify(productoRepository, times(1)).findById(99);
        verify(productoRepository, never()).save(any(Producto.class)); // Ensure save was never called
    }

    @Test
    void eliminarProducto_existente_debeEliminarYRetornarNoContent() {
        when(productoRepository.existsById(1)).thenReturn(true);
        doNothing().when(productoRepository).deleteById(1); // No behavior needed for void method

        ResponseEntity<Void> response = productoController2.eliminarProducto(1);

        assertNotNull(response);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
        verify(productoRepository, times(1)).existsById(1);
        verify(productoRepository, times(1)).deleteById(1);
    }

    @Test
    void eliminarProducto_noExistente_debeRetornarNotFound() {
        when(productoRepository.existsById(99)).thenReturn(false);

        ResponseEntity<Void> response = productoController2.eliminarProducto(99);

        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
        verify(productoRepository, times(1)).existsById(99);
        verify(productoRepository, never()).deleteById(anyInt()); // Ensure delete was never called
    }

    @Test
    void crearProductos_loteValido_debeCrearYRetornarCreated() {
        List<Producto> productosParaGuardar = Arrays.asList(producto1, producto2);
        when(productoRepository.saveAll(anyList())).thenReturn(productosParaGuardar);

        ResponseEntity<List<Producto>> response = productoController2.crearProductos(productosParaGuardar);

        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
        verify(productoRepository, times(1)).saveAll(productosParaGuardar);
    }

    @Test
    void crearProductos_loteVacio_debeRetornarBadRequest() {
        ResponseEntity<List<Producto>> response = productoController2.crearProductos(Collections.emptyList());

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNull(response.getBody());
        verify(productoRepository, never()).saveAll(anyList());
    }

    @Test
    void crearProductos_loteNulo_debeRetornarBadRequest() {
        ResponseEntity<List<Producto>> response = productoController2.crearProductos(null);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNull(response.getBody());
        verify(productoRepository, never()).saveAll(anyList());
    }
}