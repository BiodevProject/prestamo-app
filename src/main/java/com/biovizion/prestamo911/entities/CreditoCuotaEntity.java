package com.biovizion.prestamo911.entities;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "credito_cuota")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreditoCuotaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 15)
    private String codigo;

    @Column(name = "fecha_vencimiento", columnDefinition = "DATETIME")
    private LocalDateTime fechaVencimiento;

    @Column(name = "fecha_pago", columnDefinition = "DATETIME")
    private LocalDateTime fechaPago;

    @Column(precision = 10, scale = 2)
    private BigDecimal monto;

    @Column(length = 30)
    private String estado;

    @ManyToOne
    @JoinColumn(name = "credito_id", nullable = false)
    private CreditoEntity credito;
} 