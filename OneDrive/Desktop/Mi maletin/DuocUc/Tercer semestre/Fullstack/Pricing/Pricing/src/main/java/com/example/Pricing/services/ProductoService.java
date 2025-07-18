package com.example.Pricing.services;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext; // Puedes mantenerlo o eliminarlo, Spring lo maneja por constructor
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import com.example.Pricing.model.Producto;
import com.example.Pricing.repository.ProductoRepository;

@Service
public class ProductoService {

    // Elimina @PersistenceContext si lo prefieres, o mantenlo si Spring lo inyecta por detrás
    // @PersistenceContext // No es necesario si se inyecta por constructor
    private final EntityManager entityManager; // Ahora final

    private final ProductoRepository productoRepository;

    // Modifica el constructor para incluir EntityManager
    public ProductoService(ProductoRepository productoRepository, EntityManager entityManager) {
        this.productoRepository = productoRepository;
        this.entityManager = entityManager; // Asigna el EntityManager
    }

    // ... el resto de tus métodos ...

    public List<Producto> findAll() {
        return productoRepository.findAll();
    }

    public Producto obtenerProductoPorId(Integer id) {
        return productoRepository.findById(id).orElse(null);
    }

    @Transactional
    public Producto guardarProducto(Producto producto) {
        if (producto.getId() != null) {
            producto = entityManager.merge(producto);
        }
        return productoRepository.save(producto);
    }

    @Transactional
    public List<Producto> guardarProductos(List<Producto> productos) {
        return productos.stream()
            .map(producto -> producto.getId() == null ? productoRepository.save(producto) : entityManager.merge(producto))
            .toList();
    }

    public void borrarProducto(Integer id) {
        productoRepository.deleteById(id);
    }
}