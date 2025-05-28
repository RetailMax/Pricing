package com.example.Pricing.services;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.Pricing.model.Promocion;
import com.example.Pricing.repository.PromocionRepository;

@Service
public class PromocionService {

    @Autowired
    private PromocionRepository promocionRepository;

    public List<Promocion> findAll(){
        return promocionRepository.findAll();
    }

    public Promocion ObtenerPromoPorId(Integer id){
        return promocionRepository.findById(id).get();
    }

    public List<Promocion> obtenerPromocionesPorIdProducto(Integer productoid){
        return promocionRepository.buscarPromocionesPorProductoId(productoid);
    }

    public List<Promocion> obtenerPromosActivas(boolean activo){
        return promocionRepository.buscaPromoActiva(activo);
    }

    public List<Promocion> obtenerPromosPorFechaFin(LocalDateTime fechaFin){
        return promocionRepository.buscaPorFechaFin(fechaFin);
    }

    public List<Promocion> obtenerPromosPorNombre(String nombre){
        return promocionRepository.buscaPorNombre(nombre);
    }

    public Promocion guardarPromocion(Promocion promocion) {
        return promocionRepository.save(promocion);
    }

    public void borrarPromocion(Integer id){
        promocionRepository.deleteById(id);
    }

    public List<Promocion> guardarPromocionLote(List<Promocion> promociones) {
        return promocionRepository.saveAll(promociones);
    }

}
