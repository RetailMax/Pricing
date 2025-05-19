package com.example.Pricing.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "precios_base")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PrecioBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private double monto;

    @Column(nullable = false)
    private LocalDateTime fechaCreacion;

    @Column(nullable = false)
    private boolean activo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"}) 
    private Producto producto;

    public PrecioBase(double monto, LocalDateTime fechaCreacion, boolean activo, Producto producto) {
        this.monto = monto;
        this.fechaCreacion = fechaCreacion;
        this.activo = activo;
        this.producto = producto;
    }
}
