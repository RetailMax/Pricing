package com.example.Pricing.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.*;


@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Variante {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String tipo;  

    private String valor; 

    @ManyToOne
    @JoinColumn(name = "producto_id")
    private Producto producto;

    public Variante(String tipo, String valor, Producto producto) {
    this.tipo = tipo;
    this.valor = valor;
    this.producto = producto;
}

}

