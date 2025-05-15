package com.example.Pricing.services;
import com.example.Pricing.model.Producto;
import com.example.Pricing.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ProductoService {
    @Autowired
    private ProductoRepository productoRepository;

    public List<Producto> getProductos(){
        return productoRepository.obteneProductos();
    }

    public Producto saveProducto(Producto producto){
        return productoRepository.guardarProducto(producto);
    }

    public Producto geProductoId(int id){
        return productoRepository.buscarPorId(id);
    }

    public Producto updateProducto (Producto producto){
        return productoRepository.actualizarProducto(producto);
    }

    public String deleteProducto(int id){
        productoRepository.eliminar(id);
        return "producto eliminado";
    }

}
