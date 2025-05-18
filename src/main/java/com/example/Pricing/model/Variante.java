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
    private Long id;

    private String tipo;  

    private String valor; 

    @ManyToOne
    @JoinColumn(name = "producto_id")
    private Producto producto;

}

