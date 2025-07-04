package com.biovizion.prestamo911.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "usuario_solicitud")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioSolicitudEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombres;
    private String apellidos;

    @Column(name = "dui_delante")
    private String duiDelante;

    @Column(name = "dui_atras")
    private String duiAtras;

    private String nit;

    @Column(name = "fecha_nacimiento")
    private LocalDate fechaNacimiento;

    @Column(name = "estado_civil")
    private String estadoCivil;

    private String telefono;
    private String email;
    private String direccion;

    @Column(name = "empresa_trabajo")
    private String empresaTrabajo;

    private String puesto;

    @Column(name = "ingreso_mensual")
    private Double ingresoMensual;

    @Column(name = "tipo_contrato")
    private String tipoContrato;

    @Column(name = "referencia1_nombre")
    private String referencia1;

    @Column(name = "telefono_referencia1")
    private String telefonoReferencia1;

    private String parentesco1;

    @Column(name = "referencia2_nombre")
    private String referencia2;

    @Column(name = "telefono_referencia2")
    private String telefonoReferencia2;

    private String parentesco2;

    private String firma;

    @Column(name = "fecha_solicitud")
    private LocalDate fechaSolicitud;
}
