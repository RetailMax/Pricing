package com.example.Pricing.controller;


import com.example.Pricing.model.PrecioBase;
import com.example.Pricing.model.Promocion;
import com.example.Pricing.model.Variante;
import com.example.Pricing.repository.PrecioBaseRepository;
import com.example.Pricing.repository.PromocionRepository;
import com.example.Pricing.repository.VarianteRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import com.example.Pricing.services.PrecioBaseService;
import com.example.Pricing.assemblers.PricingAssembler;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
@RequestMapping("/api/preciosBase")
@Tag(name = "Pricing", description = "Controlador para la gestión de precios base y promociones")
public class PricingController {

    @Autowired
    private PrecioBaseRepository precioBaseRepository;
    @Autowired
    private PrecioBaseService precioBaseService;
    @Autowired
    private PromocionRepository promocionRepository;
    @Autowired
    private VarianteRepository varianteRepository;
    @Autowired
    private PricingAssembler pricingAssembler;

    @GetMapping
    @Operation(summary = "Obtener todos los precios base", description = "Devuelve una lista de todos los precios base registrados en el sistema.")
    @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Lista de precios obtenida correctamente"),
    @ApiResponse(responseCode = "400", description = "Solicitud inválida"),
    @ApiResponse(responseCode = "404", description = "No se encontraron precios base")
    })
    public CollectionModel<EntityModel<PrecioBase>> obtenerPrecios() {
        List<EntityModel<PrecioBase>> precios = precioBaseRepository.findAll().stream()
                .map(pricingAssembler::toModel)
                .collect(Collectors.toList());
        return CollectionModel.of(precios,
                linkTo(methodOn(PricingController.class).obtenerPrecios()).withSelfRel());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener precio base por ID", description = "Devuelve el precio base correspondiente al ID proporcionado.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Precio base encontrado"),
        @ApiResponse(responseCode = "400", description = "ID inválido"),
        @ApiResponse(responseCode = "404", description = "Precio base no encontrado")
    })
    public ResponseEntity<EntityModel<PrecioBase>> obtenerPrecioPorId(@PathVariable Integer id) {
        return precioBaseRepository.findById(id)
            .map(precio -> ResponseEntity.ok(pricingAssembler.toModel(precio)))
            .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}/activar")
    @Operation(summary = "Activar un precio base", description = "Activa el precio base correspondiente al ID proporcionado.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Precio activado correctamente"),
        @ApiResponse(responseCode = "400", description = "Solicitud inválida"),
        @ApiResponse(responseCode = "404", description = "Precio no encontrado")
    })
    public ResponseEntity<EntityModel<PrecioBase>> activarPrecio(@PathVariable Integer id) {
        Optional<PrecioBase> precio = precioBaseRepository.findById(id);
        if (precio.isPresent()) {
            PrecioBase precioBase = precio.get();
            if (!precioBase.isActivo()) {
                precioBase.setActivo(true);
                precioBaseRepository.save(precioBase);
            }
            return ResponseEntity.ok(pricingAssembler.toModel(precioBase));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}/desactivar")
    @Operation(summary = "Desactivar un precio base", description = "Desactiva el precio base correspondiente al ID proporcionado.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Precio desactivado correctamente"),
        @ApiResponse(responseCode = "400", description = "Solicitud inválida"),
        @ApiResponse(responseCode = "404", description = "Precio no encontrado")
    })
    public ResponseEntity<EntityModel<PrecioBase>> desactivarPrecio(@PathVariable Integer id) {
        Optional<PrecioBase> precio = precioBaseRepository.findById(id);
        if (precio.isPresent()) {
            PrecioBase precioBase = precio.get();
            if (precioBase.isActivo()) {
                precioBase.setActivo(false);
                precioBaseRepository.save(precioBase);
            }
            return ResponseEntity.ok(pricingAssembler.toModel(precioBase));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    @Operation(summary = "Crear un nuevo precio base", description = "Registra un nuevo precio base en el sistema.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Precio base creado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos"),
        @ApiResponse(responseCode = "404", description = "No se pudo crear el precio base")
    })
    public ResponseEntity<EntityModel<PrecioBase>> crearPrecioBase(@RequestBody PrecioBase precioBase) {
        PrecioBase nuevoPrecio = precioBaseRepository.save(precioBase);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(pricingAssembler.toModel(nuevoPrecio));
    }

    @PostMapping("/lote")
    @Operation(summary = "Crear precios base en lote", description = "Registra múltiples precios base en el sistema de una sola vez.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Precios creados exitosamente"),
        @ApiResponse(responseCode = "400", description = "Lista vacía o datos inválidos"),
        @ApiResponse(responseCode = "404", description = "No se pudieron guardar los precios")
    })
    public ResponseEntity<CollectionModel<EntityModel<PrecioBase>>> crearPreciosBaseLote(@RequestBody List<PrecioBase> preciosBase) {
        if (preciosBase == null || preciosBase.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        List<PrecioBase> preciosGuardados = precioBaseService.guardarPreciosBase(preciosBase);
        List<EntityModel<PrecioBase>> modelos = preciosGuardados.stream()
                .map(pricingAssembler::toModel)
                .collect(Collectors.toList());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(CollectionModel.of(modelos,
                        linkTo(methodOn(PricingController.class).obtenerPrecios()).withRel("todos")));
    }

    @GetMapping("/{productoId}/precioFinal")
    @Operation(summary = "Obtener precio final de un producto", description = "Calcula el precio final de un producto considerando su precio base, variantes y promociones activas.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Precio final calculado correctamente"),
        @ApiResponse(responseCode = "400", description = "Parámetros inválidos"),
        @ApiResponse(responseCode = "404", description = "Producto o variante no encontrada")
    })
    public ResponseEntity<Double> obtenerPrecioFinal(@PathVariable Integer productoId, @RequestParam(required = false) Optional<Integer> varianteId){
        PrecioBase precioBase = precioBaseRepository.findById(productoId).orElseThrow(() -> new RuntimeException("Precio no encontrado"));

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
            double descuentoAplicado = precioFinal * (porcentajeDescuento / 100.0);
            precioFinal -= descuentoAplicado;
        }

        return ResponseEntity.ok(precioFinal);
    }

