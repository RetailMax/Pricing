package com.example.Pricing.repository;

import com.example.Pricing.model.Producto;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface ProductoRepository extends JpaRepository<Producto, Integer> {

    @Query("SELECT v FROM Producto v WHERE v.id = :id")
    Producto buscaPorId(@Param("id") Integer id);

    @Query("SELECT v FROM Producto v WHERE v.nombre = :nombre")
    List<Producto> buscaPorTipo(@Param("nombre") String nombre);

}
