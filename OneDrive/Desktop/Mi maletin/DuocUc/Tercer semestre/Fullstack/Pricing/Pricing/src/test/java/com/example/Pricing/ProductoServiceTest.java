package com.example.Pricing;


import com.example.Pricing.model.Producto;
import com.example.Pricing.repository.ProductoRepository;
import com.example.Pricing.services.ProductoService;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductoServiceTest {

    @InjectMocks
    private ProductoService productoService; // Se inyectará el constructor

    @Mock
    private ProductoRepository productoRepository;

    @Mock
    private EntityManager entityManager; // Mock para EntityManager

    private Producto productoExistente;
    private Producto productoNuevo;

    @BeforeEach
    void setUp() {
        // Mocking the constructor dependency explicitly if using @InjectMocks with constructor injection
        // For @InjectMocks, Mockito will try to create the instance and pass the mocks to the constructor.
        // So, no explicit constructor call is needed here, just ensure mocks are defined.

        productoExistente = new Producto();
        productoExistente.setId(1);
        productoExistente.setNombre("Laptop Existente");
        productoExistente.setDescripcion("Descripción de laptop existente");

        productoNuevo = new Producto();
        productoNuevo.setNombre("Tablet Nueva");
        productoNuevo.setDescripcion("Descripción de tablet nueva");
    }

    @Test
    void findAll_debeRetornarTodosLosProductos() {
        // Given
        List<Producto> productos = Arrays.asList(productoExistente, productoNuevo);
        when(productoRepository.findAll()).thenReturn(productos);

        // When
        List<Producto> result = productoService.findAll();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains(productoExistente));
        assertTrue(result.contains(productoNuevo));
        verify(productoRepository, times(1)).findAll();
    }

    @Test
    void obtenerProductoPorId_existente_debeRetornarProducto() {
        // Given
        when(productoRepository.findById(1)).thenReturn(Optional.of(productoExistente));

        // When
        Producto result = productoService.obtenerProductoPorId(1);

        // Then
        assertNotNull(result);
        assertEquals(productoExistente.getId(), result.getId());
        verify(productoRepository, times(1)).findById(1);
    }

    @Test
    void obtenerProductoPorId_noExistente_debeRetornarNull() {
        // Given
        when(productoRepository.findById(99)).thenReturn(Optional.empty());

        // When
        Producto result = productoService.obtenerProductoPorId(99);

        // Then
        assertNull(result);
        verify(productoRepository, times(1)).findById(99);
    }

    @Test
    void guardarProducto_nuevoProducto_debeLlamarSave() {
        // Given: productoNuevo.getId() es null
        when(productoRepository.save(any(Producto.class))).thenReturn(productoNuevo);

        // When
        Producto result = productoService.guardarProducto(productoNuevo);

        // Then
        assertNotNull(result);
        assertEquals(productoNuevo.getNombre(), result.getNombre());
        verify(entityManager, never()).merge(any(Producto.class)); // No debería llamar a merge
        verify(productoRepository, times(1)).save(productoNuevo); // Debe llamar a save
    }

    @Test
    void guardarProducto_productoExistente_debeLlamarMergeYSave() {
        // Given: productoExistente.getId() no es null
        // Cuando merge es llamado, debería retornar el mismo objeto o una instancia manejada
        when(entityManager.merge(any(Producto.class))).thenReturn(productoExistente);
        // Cuando save es llamado después de merge, también debería retornar el objeto
        when(productoRepository.save(any(Producto.class))).thenReturn(productoExistente);


        // When
        Producto result = productoService.guardarProducto(productoExistente);

        // Then
        assertNotNull(result);
        assertEquals(productoExistente.getNombre(), result.getNombre());
        verify(entityManager, times(1)).merge(productoExistente); // Debe llamar a merge
        verify(productoRepository, times(1)).save(productoExistente); // Debe llamar a save
    }

    @Test
    void guardarProductos_listaConProductosNuevosYExistentes_debeLlamarSaveOMergeSegunCorresponda() {
        // Given
        Producto otroProductoNuevo = new Producto();
        otroProductoNuevo.setNombre("Monitor Nuevo");
        otroProductoNuevo.setDescripcion("Descripción del monitor");

        Producto otroProductoExistente = new Producto();
        otroProductoExistente.setId(2);
        otroProductoExistente.setNombre("Teclado Existente");
        otroProductoExistente.setDescripcion("Descripción del teclado");

        List<Producto> productosParaGuardar = Arrays.asList(productoNuevo, productoExistente, otroProductoNuevo, otroProductoExistente);

        // Mock para productos nuevos (id == null)
        when(productoRepository.save(otroProductoNuevo)).thenReturn(otroProductoNuevo);
        when(productoRepository.save(productoNuevo)).thenReturn(productoNuevo);

        // Mock para productos existentes (id != null)
        when(entityManager.merge(productoExistente)).thenReturn(productoExistente);
        when(entityManager.merge(otroProductoExistente)).thenReturn(otroProductoExistente);


        // When
        List<Producto> result = productoService.guardarProductos(productosParaGuardar);

        // Then
        assertNotNull(result);
        assertEquals(4, result.size());
        assertTrue(result.containsAll(productosParaGuardar)); // Verifica que todos los productos estén en el resultado

        // Verifica las llamadas a save y merge para cada tipo de producto
        verify(productoRepository, times(1)).save(productoNuevo);
        verify(productoRepository, times(1)).save(otroProductoNuevo);
        verify(entityManager, times(1)).merge(productoExistente);
        verify(entityManager, times(1)).merge(otroProductoExistente);
        // Asegúrate de que no haya llamadas "extra"
        verify(productoRepository, times(2)).save(any(Producto.class)); // Total de 2 llamadas a save para los nuevos
        verify(entityManager, times(2)).merge(any(Producto.class)); // Total de 2 llamadas a merge para los existentes
    }


    @Test
    void borrarProducto_debeLlamarDeleteById() {
        // Given
        Integer idABorrar = 1;
        doNothing().when(productoRepository).deleteById(idABorrar); // Configura el mock para métodos void

        // When
        productoService.borrarProducto(idABorrar);

        // Then
        verify(productoRepository, times(1)).deleteById(idABorrar); // Verifica que deleteById fue llamado
    }
}