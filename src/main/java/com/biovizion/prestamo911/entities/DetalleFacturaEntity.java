package com.biovizion.prestamo911.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "factura_detalle")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DetalleFacturaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "factura_id")
    private FacturaEntity factura;

    @ManyToOne
    @JoinColumn(name = "producto_id")
    private ProductoEntity producto;

    private Integer cantidad;

    @Column(name = "precio_unitario")
    private Double precioUnitario;
}
