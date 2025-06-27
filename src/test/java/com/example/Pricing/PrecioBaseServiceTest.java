package com.example.Pricing;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.example.Pricing.model.PrecioBase;
import com.example.Pricing.model.Producto;
import com.example.Pricing.services.PrecioBaseService;
import com.example.Pricing.repository.PrecioBaseRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;


@SpringBootTest
public class PrecioBaseServiceTest {

    @Autowired
    private PrecioBaseService precioBaseService;

    @MockitoBean
    private PrecioBaseRepository precioBaseRepository;

    // Helper method para crear una instancia de PrecioBase simplificada para tests
    // Esto es útil porque el constructor completo de Lombok tiene 6 parámetros
    // y el 'id' es autogenerado, y 'producto' es una dependencia.
    private PrecioBase createTestPrecioBase(Integer id, double monto, LocalDateTime fechaCreacion, boolean activo, Integer productoId) {
        PrecioBase pb = new PrecioBase();
        pb.setId(id);
        pb.setMonto(monto);
        pb.setFechaCreacion(fechaCreacion);
        pb.setActivo(activo);
        // Para el test de servicio, no siempre necesitamos un objeto Producto completo.
        // Si el servicio no interactúa directamente con las propiedades de Producto,
        // un mock simple o un Producto con solo el ID puede ser suficiente.
        if (productoId != null) {
            Producto p = new Producto(); // Asumiendo que Producto tiene un constructor sin args o setters
            p.setId(productoId);
            pb.setProducto(p);
        }
        return pb;
    }

    @Test
    void testFindAll() {
        // Arrange
        PrecioBase precio1 = createTestPrecioBase(1, 100.0, LocalDateTime.now(), true, 101);
        PrecioBase precio2 = createTestPrecioBase(2, 200.0, LocalDateTime.now(), true, 102);
        List<PrecioBase> expectedPrecios = Arrays.asList(precio1, precio2);

        when(precioBaseRepository.findAll()).thenReturn(expectedPrecios);

        // Act
        List<PrecioBase> foundPrecios = precioBaseService.findAll();

        // Assert
        assertNotNull(foundPrecios);
        assertEquals(2, foundPrecios.size());
        // Aquí puedes verificar más a fondo los contenidos si lo necesitas
        assertEquals(expectedPrecios.get(0).getMonto(), foundPrecios.get(0).getMonto());
        assertEquals(expectedPrecios.get(1).getMonto(), foundPrecios.get(1).getMonto());
        verify(precioBaseRepository, times(1)).findAll();
    }

    @Test
    void testObtenerPreciosBasePorProducto() {
        // Arrange
        Integer productoId = 101;
        PrecioBase expectedPrecio = createTestPrecioBase(1, 150.0, LocalDateTime.now(), true, productoId);

        // Notar que el método en el repositorio es findPreciosBaseByProductoId(Integer productoId)
        // Y devuelve un solo PrecioBase
        when(precioBaseRepository.findPreciosBaseByProductoId(productoId)).thenReturn(expectedPrecio);

        // Act
        PrecioBase foundPrecio = precioBaseService.obtenerPreciosBasePorProducto(productoId);

        // Assert
        assertNotNull(foundPrecio);
        assertEquals(expectedPrecio.getMonto(), foundPrecio.getMonto());
        // Verifica que el producto_id del PrecioBase encontrado coincida con el buscado
        assertNotNull(foundPrecio.getProducto());
        assertEquals(productoId, foundPrecio.getProducto().getId());
        verify(precioBaseRepository, times(1)).findPreciosBaseByProductoId(productoId);
    }

    @Test
    void testBuscarPorFecha() {
        // Arrange
        LocalDateTime testDate = LocalDateTime.of(2023, 1, 15, 10, 0, 0);
        PrecioBase precio1 = createTestPrecioBase(1, 100.0, testDate, true, 101);
        PrecioBase precio2 = createTestPrecioBase(2, 200.0, testDate, true, 102);
        List<PrecioBase> expectedPrecios = Arrays.asList(precio1, precio2);

        when(precioBaseRepository.buscaPorFecha(testDate)).thenReturn(expectedPrecios);

        // Act
        List<PrecioBase> foundPrecios = precioBaseService.buscarPorFecha(testDate);

        // Assert
        assertNotNull(foundPrecios);
        assertEquals(2, foundPrecios.size());
        assertEquals(expectedPrecios.get(0).getMonto(), foundPrecios.get(0).getMonto());
        verify(precioBaseRepository, times(1)).buscaPorFecha(testDate);
    }

    @Test
    void testBuscarPorValor() {
        // Arrange
        // Cambiado de double a float, usando 'f' para indicar el tipo float
        float testValor = 150.0f;
        PrecioBase precio1 = createTestPrecioBase(1, testValor, LocalDateTime.now(), true, 101);
        List<PrecioBase> expectedPrecios = Collections.singletonList(precio1);

        // Asegúrate de que el método buscaPorValor en tu repositorio acepte float
        when(precioBaseRepository.buscaPorValor(testValor)).thenReturn(expectedPrecios);

        // Act
        List<PrecioBase> foundPrecios = precioBaseService.buscarPorValor(testValor);

        // Assert
        assertNotNull(foundPrecios);
        assertEquals(1, foundPrecios.size());
        assertEquals(expectedPrecios.get(0).getMonto(), foundPrecios.get(0).getMonto());
        verify(precioBaseRepository, times(1)).buscaPorValor(testValor);
    }

    // ... y también ajusta el método helper createTestPrecioBase si no lo hiciste antes
    // para que el parámetro 'monto' sea un float si eso es lo que usas internamente en el test
    private PrecioBase createTestPrecioBase(Integer id, float monto, LocalDateTime fechaCreacion, boolean activo, Integer productoId) {
        PrecioBase pb = new PrecioBase();
        pb.setId(id);
        pb.setMonto(monto); // Asegúrate de que el setter de PrecioBase.setMonto acepte float
        pb.setFechaCreacion(fechaCreacion);
        pb.setActivo(activo);
        if (productoId != null) {
            Producto p = new Producto();
            p.setId(productoId);
            pb.setProducto(p);
        }
        return pb;
    }
    @Test
    void testGuardarPrecioBase() {
        // Arrange
        // No le damos un ID, ya que es autogenerado en la DB.
        PrecioBase precioToSave = createTestPrecioBase(null, 120.0, LocalDateTime.now(), true, 103);
        // Simulamos que el repositorio le asigna un ID al guardarlo
        PrecioBase savedPrecioWithId = createTestPrecioBase(10, 120.0, precioToSave.getFechaCreacion(), true, 103);


        when(precioBaseRepository.save(precioToSave)).thenReturn(savedPrecioWithId);

        // Act
        PrecioBase savedPrecio = precioBaseService.guardarPrecioBase(precioToSave);

        // Assert
        assertNotNull(savedPrecio);
        // Verificamos que el ID ya no es null, simulando el comportamiento de la DB
        assertNotNull(savedPrecio.getId());
        assertEquals(10, savedPrecio.getId());
        assertEquals(precioToSave.getMonto(), savedPrecio.getMonto());
        verify(precioBaseRepository, times(1)).save(precioToSave);
    }

    @Test
    void testGuardarPreciosBase() {
        // Arrange
        PrecioBase precio1 = createTestPrecioBase(null, 100.0, LocalDateTime.now(), true, 101);
        PrecioBase precio2 = createTestPrecioBase(null, 200.0, LocalDateTime.now(), true, 102);
        List<PrecioBase> preciosToSave = Arrays.asList(precio1, precio2);

        // Simulamos que el repositorio les asigna IDs al guardarlos
        PrecioBase savedPrecio1 = createTestPrecioBase(11, 100.0, precio1.getFechaCreacion(), true, 101);
        PrecioBase savedPrecio2 = createTestPrecioBase(12, 200.0, precio2.getFechaCreacion(), true, 102);
        List<PrecioBase> savedPreciosWithIds = Arrays.asList(savedPrecio1, savedPrecio2);

        when(precioBaseRepository.saveAll(preciosToSave)).thenReturn(savedPreciosWithIds);

        // Act
        List<PrecioBase> savedPrecios = precioBaseService.guardarPreciosBase(preciosToSave);

        // Assert
        assertNotNull(savedPrecios);
        assertEquals(2, savedPrecios.size());
        assertNotNull(savedPrecios.get(0).getId()); // Verificar que tienen ID
        assertNotNull(savedPrecios.get(1).getId());
        assertEquals(savedPreciosWithIds.get(0).getMonto(), savedPrecios.get(0).getMonto());
        assertEquals(savedPreciosWithIds.get(1).getMonto(), savedPrecios.get(1).getMonto());
        verify(precioBaseRepository, times(1)).saveAll(preciosToSave);
    }
}

