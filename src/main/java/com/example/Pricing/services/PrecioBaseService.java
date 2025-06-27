package com.example.Pricing.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import com.example.Pricing.model.PrecioBase;
import com.example.Pricing.repository.PrecioBaseRepository;

import com.example.Pricing.repository.ProductoRepository;
import com.example.Pricing.model.Producto;
import com.example.Pricing.model.PrecioBase;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class PrecioBaseService {

    @Autowired
    private PrecioBaseRepository precioBaseRepository;

    @Autowired
    private ProductoRepository productoRepository;

    public List<PrecioBase> findAll(){
        return precioBaseRepository.findAll();
    }

    public PrecioBase obtenerPreciosBasePorProducto(Integer productoId) {
    return precioBaseRepository.findPreciosBaseByProductoId(productoId);
    }

    public List<PrecioBase> buscarPorFecha(LocalDateTime  fechaCreacion) {
        return precioBaseRepository.buscaPorFecha(fechaCreacion);
    }

    public List<PrecioBase> buscarPorValor(Float valor) {
        return precioBaseRepository.buscaPorValor(valor);
    }

    public PrecioBase guardarPrecioBase(PrecioBase precioBase) {
        return precioBaseRepository.save(precioBase);
    }

    public List<PrecioBase> guardarPreciosBase(List<PrecioBase> preciosBase) {
        return precioBaseRepository.saveAll(preciosBase);
    }


     @Transactional
    public PrecioBase actualizarPrecioProducto(Integer productoId, double nuevoMonto) {
        Producto producto = productoRepository.findById(productoId)
                .orElseThrow(() -> new RuntimeException("Error: El producto con ID " + productoId + " no existe."));

        Optional<PrecioBase> precioAntiguoOpt = precioBaseRepository.findFirstByProductoIdAndActivoIsTrueOrderByFechaCreacionDesc(productoId);
        
        if (precioAntiguoOpt.isPresent()) {
            PrecioBase precioAntiguo = precioAntiguoOpt.get();
            precioAntiguo.setActivo(false);
            precioBaseRepository.save(precioAntiguo);
        }

        PrecioBase nuevoPrecio = new PrecioBase();
        nuevoPrecio.setProducto(producto);
        nuevoPrecio.setMonto(nuevoMonto);
        nuevoPrecio.setFechaCreacion(LocalDateTime.now());
        nuevoPrecio.setActivo(true);

        return precioBaseRepository.save(nuevoPrecio);
    }
    
    public List<PrecioBase> obtenerHistorialDePreciosProducto(Integer productoId) {
        return precioBaseRepository.findPreciosBaseByProductoId(productoId);
    }
}