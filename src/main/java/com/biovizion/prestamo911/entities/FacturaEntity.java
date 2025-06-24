package com.biovizion.prestamo911.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;


import jakarta.persistence.*;
import lombok.*;
import java.util.Date;

@Entity
@Table(name = "factura")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FacturaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Date fechaEmision;
    private Date fechaCancelado;

    @ManyToOne
    @JoinColumn(name = "comprador_id")
    private UsuarioEntity comprador;

    @ManyToOne
    @JoinColumn(name = "creado_por")
    private UsuarioEntity creadoPor;

    private String estado;
}
