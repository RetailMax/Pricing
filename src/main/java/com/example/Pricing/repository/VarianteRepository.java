package com.example.Pricing.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.Pricing.model.Variante;

public interface VarianteRepository extends JpaRepository<Variante, Integer> {
}