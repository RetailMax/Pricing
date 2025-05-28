package com.example.Pricing.controller;

import com.example.Pricing.model.PrecioBase;
import com.example.Pricing.model.Promocion;
import com.example.Pricing.model.Variante;
import com.example.Pricing.repository.PrecioBaseRepository;
import com.example.Pricing.repository.PromocionRepository;
import com.example.Pricing.repository.VarianteRepository;
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
    private final PrecioBaseService precioBaseService;
    private final PromocionRepository promocionRepository;
    private final VarianteRepository varianteRepository;



    public PricingController(PrecioBaseRepository precioBaseRepository, PrecioBaseService precioBaseService, PromocionRepository promocionRepository, VarianteRepository varianteRepository) {
        this.precioBaseRepository = precioBaseRepository;
        this.precioBaseService = precioBaseService;
        this.promocionRepository = promocionRepository;
        this.varianteRepository = varianteRepository;
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

    @GetMapping("/{productoId}/precioFinal")
    public ResponseEntity<Double> obtenerPrecioFinal(@PathVariable Integer productoId, @RequestParam(required = false) Optional<Integer> varianteId){
        
        PrecioBase precioBase = precioBaseRepository.findById(productoId).orElseThrow(()-> new RuntimeException("Precio no encontrado"));

        double precioFinal = precioBase.getMonto();
        double agregadoPorVariante = 0.0;

        if (varianteId.isPresent()){
            Variante variante = varianteRepository.findById(varianteId.get()).orElseThrow(() -> new RuntimeException("Variante no encontrada"));
            agregadoPorVariante = variante.getValor();
            precioFinal += agregadoPorVariante;
        }

        Optional<Promocion> promocionActiva = promocionRepository.findByProductoIdAndActivoTrue(productoId);

        if (promocionActiva.isPresent()) {
            Promocion promocion = promocionActiva.get();
            double porcentajeDescuento = promocion.getPorcentajeDescuento();
            double descuentoAplicado = precioFinal * (porcentajeDescuento/100.0);
            precioFinal -= descuentoAplicado;
        }

        return ResponseEntity.ok(precioFinal);


    }

    
}