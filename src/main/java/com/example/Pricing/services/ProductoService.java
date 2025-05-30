package com.example.Pricing.services;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import com.example.Pricing.model.Producto;
import com.example.Pricing.repository.ProductoRepository;

@Service
public class ProductoService {

    @PersistenceContext
    private EntityManager entityManager;

    private final ProductoRepository productoRepository;

    public ProductoService(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

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