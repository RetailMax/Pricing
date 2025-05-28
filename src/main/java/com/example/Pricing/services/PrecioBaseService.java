package com.example.Pricing.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Map;
import com.example.Pricing.model.PrecioBase;
import com.example.Pricing.repository.PrecioBaseRepository;

@Service
public class PrecioBaseService {

    @Autowired
    private PrecioBaseRepository precioBaseRepository;

    public List<PrecioBase> findAll(){
        return precioBaseRepository.findAll();
    }

    public List<Map<String, Object>> obtenerPreciosBasePorProducto(Integer varianteId) {
    return precioBaseRepository.findPreciosBaseByVarianteId(varianteId)
        .stream()
        .map(precio -> {
            Map<String, Object> map = new java.util.HashMap<>();
            map.put("id", precio.getId());
            map.put("monto", precio.getMonto());
            map.put("activo", precio.isActivo() ? "Activo" : "Inactivo");
            map.put("fechaCreacion", precio.getFechaCreacion());
            return map;
        })
        .collect(Collectors.toList());
    }

    public List<PrecioBase> buscarPorFecha(LocalDateTime  fechaCreacion) {
        return precioBaseRepository.buscaPorFecha(fechaCreacion);
    }

    public List<PrecioBase> buscarPorValor(Float valor) {
        return precioBaseRepository.buscaPorValor(valor);
    }

    public PrecioBase guardarPrecioBase(PrecioBase precioBase) {
        return precioBaseRepository.save(precioBase);
    }

    public List<PrecioBase> guardarPreciosBase(List<PrecioBase> preciosBase) {
        return precioBaseRepository.saveAll(preciosBase);
    }

}