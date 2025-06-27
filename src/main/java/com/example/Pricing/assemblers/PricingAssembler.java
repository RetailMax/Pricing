package com.example.Pricing.assemblers;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import com.example.Pricing.model.PrecioBase;
import com.example.Pricing.controller.PricingController;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

@Component
public class PricingAssembler implements RepresentationModelAssembler<PrecioBase, EntityModel<PrecioBase>> {

    @Override
    public EntityModel<PrecioBase> toModel(PrecioBase entity) {
        return EntityModel.of(entity,
            linkTo(methodOn(PricingController.class).obtenerPrecioPorId(entity.getId())).withSelfRel(),
            linkTo(methodOn(PricingController.class).obtenerPrecios()).withRel("todos"),
            linkTo(methodOn(PricingController.class).activarPrecio(entity.getId())).withRel("activar"),
            linkTo(methodOn(PricingController.class).desactivarPrecio(entity.getId())).withRel("desactivar")
        );
}
}
