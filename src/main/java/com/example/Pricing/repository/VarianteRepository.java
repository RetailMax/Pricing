package com.example.Pricing.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.example.Pricing.model.Variante;
import java.util.List;

@Repository
public interface VarianteRepository extends JpaRepository<Variante, Integer> {

    @Query("SELECT v FROM Variante v WHERE v.id = :id")
    Variante buscaPorId(@Param("id") Integer id);

    @Query("SELECT v FROM Variante v WHERE v.tipo = :tipo")
    List<Variante> buscaPorTipo(@Param("tipo") String tipo);

    @Query("SELECT v FROM Variante v WHERE v.valor = :valor")
    List<Variante> buscaPorValor(@Param("valor") Float valor);


}
