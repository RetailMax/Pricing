package com.example.Pricing.controller;


import com.example.Pricing.model.Variante;
import com.example.Pricing.services.VarianteService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/variantes")
public class VarianteController {

    @Autowired
    private VarianteService varianteService;

    @GetMapping
    public ResponseEntity<List<Variante>> obtenerVariantes() {
        List<Variante> variantes = varianteService.findAll();
        return variantes.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(variantes);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Variante> obtenerVariantePorId(@PathVariable Integer id) {
        Variante variante = varianteService.ObtenerProVariantePorId(id);
        return variante != null ? ResponseEntity.ok(variante) : ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<Variante> crearVariante(@RequestBody Variante variante) {
        Variante nuevaVariante = varianteService.guardarVariante(variante);
        return ResponseEntity.status(201).body(nuevaVariante);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Variante> actualizarVariante(@PathVariable Integer id, @RequestBody Variante varianteActualizada) {
        Optional<Variante> varianteExistente = Optional.ofNullable(varianteService.ObtenerProVariantePorId(id));
        if (!varianteExistente.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        Variante variante = varianteExistente.get();
        variante.setTipo(varianteActualizada.getTipo());
        variante.setValor(varianteActualizada.getValor());
        variante.setProducto(varianteActualizada.getProducto());

        return ResponseEntity.ok(varianteService.guardarVariante(variante));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarVariante(@PathVariable Integer id) {
        if (varianteService.ObtenerProVariantePorId(id) == null) {
            return ResponseEntity.notFound().build();
        }
        varianteService.borrarVariante(id);
        return ResponseEntity.noContent().build();
    }

@   PostMapping("/lote")
    public ResponseEntity<List<Variante>> crearVariantesLote(@RequestBody List<Variante> variantes) {
    if (variantes == null || variantes.isEmpty()) {
        return ResponseEntity.badRequest().body(null);
    }

    List<Variante> variantesGuardadas = varianteService.guardarVariantes(variantes);
    return ResponseEntity.status(HttpStatus.CREATED).body(variantesGuardadas);
    }

    

}