package com.example.Pricing.repository;

import com.example.Pricing.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ProductoRepository extends JpaRepository<Producto, Integer> {
}
