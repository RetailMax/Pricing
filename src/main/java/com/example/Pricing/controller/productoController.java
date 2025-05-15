package com.example.Pricing.controller;

import com.example.Pricing.model.Producto;
import com.example.Pricing.services.ProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping("api/v1/Productos")
public class productoController {
    @Autowired
    private ProductoService productoService;

    @GetMapping
    public List<Producto> listaProductos(){
        return productoService.getProductos();
    }
    
    @PostMapping
    public Producto agregaProducto(@RequestBody Producto producto){
        return productoService.saveProducto(producto);
    }

    @GetMapping("{id}")
    public Producto buscaProducto(@PathVariable int id){
        return productoService.geProductoId(id);
    }

    @PutMapping("{id}")
    public Producto actualizarProducto(@PathVariable int id, @RequestBody Producto producto){
        return productoService.updateProducto(producto);
    }

    @DeleteMapping("{id}")
    public String eliminarProducto(@PathVariable int id){
        return productoService.deleteProducto(id);
    }


}
