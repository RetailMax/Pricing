package com.example.Pricing.repository;

import com.example.Pricing.model.Producto;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class ProductoRepository {

    private List<Producto> listaProductos = new ArrayList<>();


    public List<Producto> obteneProductos(){
        return listaProductos;
    }

    public Producto buscarPorId(int id){
        for (Producto producto : listaProductos){
            if (producto.getId() == id){
                return producto;
            }
        }
        return null;
    }

    public Producto guardarProducto(Producto pro){
        listaProductos.add(pro);
        return pro;
    }

    public Producto actualizarProducto(Producto pro){
        int id = 0;
        int idPosicion= 0;

        for (int i=0; i<listaProductos.size();i++){
            if (listaProductos.get(1).getId() == pro.getId()){
                id = pro.getId();
                idPosicion = 1;

            }
        }

        Producto producto1 = new Producto();
        producto1.setId(id);
        producto1.setNombre(pro.getNombre());
        producto1.setDescripcion(pro.getDescripcion());

        listaProductos.set(idPosicion, producto1);
        return producto1;
    }

    public void eliminar(int id){
        Producto producto = buscarPorId(id);
        if (producto != null){
            listaProductos.remove(producto);
        }
    }

}