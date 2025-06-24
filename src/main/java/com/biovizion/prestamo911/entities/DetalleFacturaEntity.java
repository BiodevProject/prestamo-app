package com.biovizion.prestamo911.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "detallefactura")
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

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    private Integer cantidad;

    private Double precioUnitario;

    private Double descuento;

    private Double iva;

    private Double subtotal;

    private Double total;
}
