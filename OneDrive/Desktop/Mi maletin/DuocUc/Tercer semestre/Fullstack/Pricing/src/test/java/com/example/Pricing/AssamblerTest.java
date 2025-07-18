package com.example.Pricing;


import com.example.Pricing.assemblers.PricingAssembler;
import com.example.Pricing.controller.PricingController;
import com.example.Pricing.model.PrecioBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

class PricingAssemblerTest {

    private PricingAssembler pricingAssembler;
    private PrecioBase precioBase;

    @BeforeEach
    void setUp() {
        pricingAssembler = new PricingAssembler(); // No hay dependencias que mockear
        precioBase = new PrecioBase();
        precioBase.setId(1);
        precioBase.setMonto(100.0);
        // No es necesario mockear otros atributos a menos que afecten la generación de enlaces
    }

    @Test
    void toModel_debeGenerarEnlacesCorrectos() {
        // When
        EntityModel<PrecioBase> model = pricingAssembler.toModel(precioBase);

        // Then
        assertNotNull(model);
        assertEquals(precioBase, model.getContent());

        // Verificar el enlace 'self'
        Link selfLink = model.getLink("self").orElse(null);
        assertNotNull(selfLink);
        // Usamos linkTo(methodOn(...)) para generar el enlace esperado y compararlo
        assertEquals(linkTo(methodOn(PricingController.class).obtenerPrecioPorId(precioBase.getId())).withSelfRel().getHref(), selfLink.getHref());

        // Verificar el enlace 'todos'
        Link todosLink = model.getLink("todos").orElse(null);
        assertNotNull(todosLink);
        assertEquals(linkTo(methodOn(PricingController.class).obtenerPrecios()).withRel("todos").getHref(), todosLink.getHref());

        // Verificar el enlace 'activar'
        Link activarLink = model.getLink("activar").orElse(null);
        assertNotNull(activarLink);
        assertEquals(linkTo(methodOn(PricingController.class).activarPrecio(precioBase.getId())).withRel("activar").getHref(), activarLink.getHref());

        // Verificar el enlace 'desactivar'
        Link desactivarLink = model.getLink("desactivar").orElse(null);
        assertNotNull(desactivarLink);
        assertEquals(linkTo(methodOn(PricingController.class).desactivarPrecio(precioBase.getId())).withRel("desactivar").getHref(), desactivarLink.getHref());


    }
}