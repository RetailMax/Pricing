package com.example.Pricing.services;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;
import com.example.Pricing.model.Producto;
import com.example.Pricing.model.Promocion;
import com.example.Pricing.repository.PromocionRepository;
import com.example.Pricing.repository.ProductoRepository;
@Service
public class PromocionService {

    @Autowired
    private PromocionRepository promocionRepository;
    @Autowired
    private ProductoRepository productoRepository;

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

    @Transactional
    public Promocion guardarPromocion(Promocion promocion) {
        if (promocion.getId() != null && promocion.getId() == 0) {
            promocion.setId(null);
        }
        
        if (promocion.getProducto() != null && promocion.getProducto().getId() != null) {
            Optional<Producto> productoExistente = productoRepository.findById(promocion.getProducto().getId());
            if (productoExistente.isPresent()) {
                promocion.setProducto(productoExistente.get()); 
            } else {
                throw new RuntimeException("El producto con ID " + promocion.getProducto().getId() + " no existe.");
            }
        } else {
            promocion.setProducto(null);
        }

        return promocionRepository.save(promocion);
    }

    public void borrarPromocion(Integer id){
        promocionRepository.deleteById(id);
    }

    public List<Promocion> guardarPromocionLote(List<Promocion> promociones) {
        return promocionRepository.saveAll(promociones);
    }

}
