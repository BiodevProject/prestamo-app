package com.biovizion.prestamo911.entities;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "credito")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreditoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(precision = 10, scale = 2)
    private BigDecimal monto;

    @Column(precision = 10, scale = 2)
    private BigDecimal abono;

    private String estado = "pendiente";


    @Column(name = "proximo_pago")
    private LocalDate proximoPago;

    @Column(name = "plazo_meses")
    private Integer plazoMeses;

    @Column(columnDefinition = "TEXT")
    private String destino;


    @Column(name = "forma_de_pago")
    private String formaDePago;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private UsuarioEntity usuario;


    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "usuario_solicitud_id")
    private UsuarioSolicitudEntity usuarioSolicitud = new UsuarioSolicitudEntity();

    // Porcentajes configurables por el admin
    @Column(name = "porcentaje_interes", precision = 5, scale = 2)
    private BigDecimal porcentajeInteres;

    @Column(name = "porcentaje_mora", precision = 5, scale = 2)
    private BigDecimal porcentajeMora;

    @Column(name = "porcentaje_iva", precision = 5, scale = 2)
    private BigDecimal porcentajeIva;

    @Column(name = "comision_fija", precision = 10, scale = 2)
    private BigDecimal comisionFija;

    // Montos automaticamente calculados
    @Column(precision = 10, scale = 2)
    private BigDecimal interes;

    @Column(precision = 10, scale = 2)
    private BigDecimal mora;

    @Column(precision = 10, scale = 2)
    private BigDecimal iva;

    @Column(precision = 10, scale = 2)
    private BigDecimal total;

    @Column(name = "cuota_mensual", precision = 10, scale = 2)
    private BigDecimal cuotaMensual;

    // Fechas de estado
    @Column(name = "fecha_aceptado", columnDefinition = "DATETIME")
    private LocalDateTime fechaAceptado;

    @Column(name = "fecha_finalizado", columnDefinition = "DATETIME")
    private LocalDateTime fechaFinalizado;

    @OneToMany(mappedBy = "credito", cascade = CascadeType.ALL)
    private List<CreditoCuotaEntity> cuotas = new ArrayList<>();
}
