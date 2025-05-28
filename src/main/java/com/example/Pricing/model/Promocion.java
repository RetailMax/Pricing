package com.example.Pricing.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "promociones")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Promocion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String nombre;
    
    @Column(nullable = false)
    private double porcentajeDescuento;
    
    @Column(nullable = false)
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;
    
    @Column(nullable = true)
    private Integer limiteProductos; 
    
    @Column(nullable = true)
    private Integer limiteUsuarios; 
    @ManyToOne
    @JoinColumn(name = "producto_id", nullable = true)
    private Producto producto;

    @Column(nullable = true)
    private String categoria; 

    @Column(nullable = false)
    private boolean activo;
}
