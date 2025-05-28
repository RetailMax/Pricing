package com.example.Pricing.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import com.example.Pricing.model.Producto;
import com.example.Pricing.repository.ProductoRepository;


@Service
public class ProductoService {

    @Autowired
    private ProductoRepository productoRepository;

    public List<Producto> findAll(){
        return productoRepository.findAll();
    }

    public Producto ObtenerProductoPorId(Integer id){
        return productoRepository.findById(id).get();
    }

    public Producto guardarProducto(Producto producto) {
        return productoRepository.save(producto);
    }

    public void borrarProducto(Integer id){
        productoRepository.deleteById(id);
    }

}
