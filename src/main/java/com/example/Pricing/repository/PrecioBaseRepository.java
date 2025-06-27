package com.example.Pricing.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.Pricing.model.PrecioBase;

public interface PrecioBaseRepository extends JpaRepository<PrecioBase, Integer> {

    @Query("SELECT p FROM PrecioBase p")
    List<PrecioBase> findAllPreciosBase();

    @Query("SELECT p FROM PrecioBase p WHERE p.producto.id = :productoId")
    PrecioBase findPreciosBaseByProductoId(@Param("productoId") Integer productoId);

    @Query("SELECT v FROM PrecioBase v WHERE v.fechaCreacion = :fechaCreacion")
    List<PrecioBase> buscaPorFecha(@Param("fechaCreacion") LocalDateTime fechaCreacion);

    @Query("SELECT v FROM PrecioBase v WHERE v.monto = :monto")
    List<PrecioBase> buscaPorValor(@Param("monto") Float monto);

    Optional<PrecioBase> findFirstByProductoIdAndActivoIsTrueOrderByFechaCreacionDesc(Integer productoId);


}