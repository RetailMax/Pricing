package com.example.Pricing.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.Pricing.model.PrecioBase;

public interface PrecioBaseRepository extends JpaRepository<PrecioBase, Integer> {
}