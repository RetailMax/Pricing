package com.example.Pricing
;

import com.example.Pricing.model.Producto;
import com.example.Pricing.model.PrecioBase;
import com.example.Pricing.model.Promocion;
import com.example.Pricing.model.Variante;
import com.example.Pricing.repository.ProductoRepository;
import com.example.Pricing.repository.PrecioBaseRepository;
import com.example.Pricing.repository.PromocionRepository;
import com.example.Pricing.repository.VarianteRepository;

import net.datafaker.Faker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@Profile("dev")
@Component
public class DataLoader implements CommandLineRunner {

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private PrecioBaseRepository precioBaseRepository;

    @Autowired
    private PromocionRepository promocionRepository;

    @Autowired
    private VarianteRepository varianteRepository;

    @Override
    public void run(String... args) throws Exception {
        Faker faker = new Faker();
        Random random = new Random();


        for (int i = 0; i < 10; i++) {
            Producto producto = new Producto();
            producto.setNombre(faker.commerce().productName());
            producto.setDescripcion(faker.lorem().sentence());
            productoRepository.save(producto);
        }

        List<Producto> productos = productoRepository.findAll();


        for (Producto producto : productos) {
            PrecioBase precio = new PrecioBase();
            precio.setMonto(faker.number().randomDouble(2, 1000, 100000));
            precio.setFechaCreacion(LocalDateTime.now().minusDays(random.nextInt(30)));
            precio.setActivo(true);
            precio.setProducto(producto);
            precioBaseRepository.save(precio);
        }


        for (Producto producto : productos) {
            Promocion promo = new Promocion();
            promo.setNombre("Promo " + faker.beer().name());
            promo.setPorcentajeDescuento(random.nextInt(50) + 1);
            promo.setFechaInicio(LocalDateTime.now());
            promo.setFechaFin(LocalDateTime.now().plusDays(random.nextInt(30) + 1));
            promo.setLimiteProductos(random.nextInt(100));
            promo.setLimiteUsuarios(random.nextInt(1000));
            promo.setCategoria(faker.commerce().department());
            promo.setActivo(true);
            promo.setProducto(producto);
            promocionRepository.save(promo);
        }


        String[] tipos = {"Color", "Tamaño", "Peso", "Sabor"};
        for (Producto producto : productos) {
            Variante variante = new Variante();
            variante.setTipo(tipos[random.nextInt(tipos.length)]);
            variante.setValor(faker.number().randomDouble(2, 1, 100));
            variante.setProducto(producto);
            varianteRepository.save(variante);
        }
    }
}
