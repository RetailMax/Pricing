package com.example.Pricing.repository;

import com.example.Pricing.model.Promocion;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface PromocionRepository extends JpaRepository<Promocion, Integer> {

    @Query("SELECT p FROM Promocion p WHERE p.id = :id")
    Promocion buscaPorId(@Param("id") Integer id);

    @Query("SELECT p FROM Promocion p WHERE p.producto.id = :productoId")
    List<Promocion> buscarPromocionesPorProductoId(@Param("productoId") Integer productoId);

    @Query("SELECT p FROM Promocion p WHERE p.activo = :activo")
    List<Promocion> buscaPromoActiva(@Param("activo") boolean activo);

    @Query("SELECT p FROM Promocion p WHERE p.fechaFin = :fechaFin")
    List<Promocion> buscaPorFechaFin(@Param("fechaFin") LocalDateTime fechaFin);

    @Query("SELECT p FROM Promocion p WHERE p.nombre = :nombre")
    List<Promocion> buscaPorNombre(@Param("nombre") String nombre);

}

