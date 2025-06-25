package com.biovizion.prestamo911.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "credito")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreditoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double monto;

    @Column(name = "plazo_meses")
    private Integer plazoMeses;

    @Column(columnDefinition = "TEXT")
    private String destino;

    @Column(name = "forma_de_pago")
    private String formaDePago;
    
    @OneToOne(cascade = CascadeType.ALL)  // or @ManyToOne depending on your model
    @JoinColumn(name = "usuario_solicitud_id")
    private UsuarioSolicitudEntity usuarioSolicitud = new UsuarioSolicitudEntity();
}
