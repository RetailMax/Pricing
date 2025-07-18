package com.example.Pricing.controller;

import com.example.Pricing.model.Promocion;
import com.example.Pricing.repository.PromocionRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;
import com.example.Pricing.services.PromocionService;


@RestController
@RequestMapping("/api/promociones")
public class PromocionController {

    @Autowired
    private PromocionRepository promocionRepository;
    @Autowired
    private PromocionService promocionService;

    @GetMapping
    public List<Promocion> obtenerPromociones() {
        return promocionRepository.findAll();
    }

    @PostMapping
    public ResponseEntity<Promocion> crearPromocion(@RequestBody Promocion promocion) {
    Promocion nuevaPromo = promocionRepository.save(promocion);
    return ResponseEntity.status(HttpStatus.CREATED).body(nuevaPromo);
    }

    @GetMapping("/{id}")
    public Promocion obtenerPromoPorId(@PathVariable Integer id) {
        return promocionRepository.findById(id).orElse(null);
    }

    @PutMapping("/{id}/switch")
    public ResponseEntity<String> actualizarEstado(@PathVariable Integer id) {
    Optional<Promocion> promocionOpt = promocionRepository.findById(id); 

        if (promocionOpt.isPresent()) {
            Promocion promocion = promocionOpt.get();
            promocion.setActivo(!promocion.isActivo()); 
            promocionRepository.save(promocion);
            return ResponseEntity.ok("Estado actualizado a " + promocion.isActivo());
        } else {
            return ResponseEntity.notFound().build();
        }
    }


    @PostMapping("/lote")
    public ResponseEntity<List<Promocion>> crearPromocionLote(@RequestBody List<Promocion> promocion) {
    if (promocion == null || promocion.isEmpty()) {
        return ResponseEntity.badRequest().build();
    }

    List<Promocion> promocionesGuardadas = promocionService.guardarPromocionLote(promocion);
    return ResponseEntity.status(HttpStatus.CREATED).body(promocionesGuardadas);
    }

    
}