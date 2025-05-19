package com.example.Pricing.controller;
import com.example.Pricing.model.PrecioBase; 
import com.example.Pricing.repository.PrecioBaseRepository; 
import org.springframework.http.ResponseEntity; 
import org.springframework.web.bind.annotation.*; 
import java.util.List; 
import java.util.Optional; 


@RestController 
@RequestMapping("/api/precios") 
public class PricingController {
    private final PrecioBaseRepository precioBaseRepository;
    public PricingController(PrecioBaseRepository precioBaseRepository) {
        this.precioBaseRepository = precioBaseRepository;
    }


    @GetMapping
    public List<PrecioBase> obtenerPrecios() {
        return precioBaseRepository.findAll();
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

 
}
