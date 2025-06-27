package com.example.Pricing.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import com.example.Pricing.model.Variante;
import com.example.Pricing.repository.VarianteRepository;

@Service
public class VarianteService {

    @Autowired
    private VarianteRepository varianteRepository;

    public List<Variante> findAll(){
        return varianteRepository.findAll();
    }

    public Variante ObtenerProVariantePorId(Integer id){
        return varianteRepository.findById(id).get();
    }

    public Variante guardarVariante(Variante variante) {
        return varianteRepository.save(variante);
    }

    public void borrarVariante(Integer id){
        varianteRepository.deleteById(id);
    }

    public List<Variante> guardarVariantes(List<Variante> variantes) {
        return varianteRepository.saveAll(variantes);
    }


}
