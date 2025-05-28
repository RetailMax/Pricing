package com.example.Pricing.controller;

import com.example.Pricing.model.PrecioBase;
import com.example.Pricing.repository.PrecioBaseRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;
import com.example.Pricing.services.PrecioBaseService;


@RestController
@RequestMapping("/api/preciosBase")
public class PricingController {

    private final PrecioBaseRepository precioBaseRepository;
    private final PrecioBaseService precioBaseService; // Declara el servicio

    // Inyecta ambos repositorios y servicios en el constructor
    public PricingController(PrecioBaseRepository precioBaseRepository, PrecioBaseService precioBaseService) {
        this.precioBaseRepository = precioBaseRepository;
        this.precioBaseService = precioBaseService; // Asigna el servicio inyectado
    }


    @GetMapping
    public List<PrecioBase> obtenerPrecios() {
        return precioBaseRepository.findAll();
    }

    @PostMapping
    public ResponseEntity<PrecioBase> crearPrecioBase(@RequestBody PrecioBase precioBase) {
    PrecioBase nuevoPrecio = precioBaseRepository.save(precioBase);
    return ResponseEntity.status(HttpStatus.CREATED).body(nuevoPrecio);
    }

    @GetMapping("/{id}")
    public PrecioBase obtenerPrecioPorId(@PathVariable Integer id) {
        return precioBaseRepository.findById(id).orElse(null);
    }

    @PutMapping("/{id}/switch")
    public ResponseEntity<String> actualizarEstado(@PathVariable Integer id) {
        Optional<PrecioBase> precio = precioBaseRepository.findById(id);
        if (precio.isPresent()) {
            PrecioBase precioBase = precio.get();
            precioBase.setActivo(!precioBase.isActivo());
            precioBaseRepository.save(precioBase);
            return ResponseEntity.ok("Estado actualizado a " + precioBase.isActivo());
        } else {
            return ResponseEntity.notFound().build();
        }
    }


    @PostMapping("/lote")
    public ResponseEntity<List<PrecioBase>> crearPreciosBaseLote(@RequestBody List<PrecioBase> preciosBase) {
    if (preciosBase == null || preciosBase.isEmpty()) {
        return ResponseEntity.badRequest().build();
    }

    List<PrecioBase> preciosGuardados = precioBaseService.guardarPreciosBase(preciosBase);
    return ResponseEntity.status(HttpStatus.CREATED).body(preciosGuardados);
    }
}