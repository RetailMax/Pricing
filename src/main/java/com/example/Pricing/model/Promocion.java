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

    @Version
    private Integer version;

    @Column(nullable = false)
    private String nombre;
    
    @Column(nullable = false)
    private double porcentajeDescuento;
    
    @Column(nullable = false)
    private LocalDateTime fechaInicio;

    @Column(nullable = false)
    private LocalDateTime fechaFin;
    
    @Column(nullable = true)
    private Integer limiteProductos; 
    
    @Column(nullable = true)
    private Integer limiteUsuarios; 
    @ManyToOne
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;

    @Column(nullable = true)
    private String categoria; 

    @Column(nullable = false)
    private boolean activo;
}
