package com.example.Pricing.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Optional;
import com.example.Pricing.model.Promocion;
import com.example.Pricing.repository.PromocionRepository;

@RestController
@RequestMapping("/api/promociones")
public class PromoController { 

    private final PromocionRepository promocionRepository;

    public PromoController(PromocionRepository promocionRepository) {
        this.promocionRepository = promocionRepository;
        System.out.println("PromoController ha sido inicializado correctamente.");

    }

    @GetMapping("/{id}")
    public ResponseEntity<Promocion> obtenerPromoPorId(@PathVariable Integer id) {
        return promocionRepository.findById(id)
            .map(ResponseEntity::ok)
            .orElseGet(() -> ResponseEntity.notFound().build());
    }

    

    @PutMapping("/{id}/switch")
    public ResponseEntity<String> actualizarPromo(@PathVariable Integer id) {
        Optional<Promocion> promo = promocionRepository.findById(id);
        if (promo.isPresent()) {
            Promocion promocion = promo.get();
            promocion.setActivo(!promocion.isActivo());
            promocionRepository.save(promocion);
            return ResponseEntity.ok("Estado actualizado a " + promocion.isActivo());
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}