package com.biovizion.prestamo911.entities;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "alerta")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AlertaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String codigo;

    @Column(nullable = false, length = 50, name = "tipo_alerta")
    private String tipo;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String mensaje;


    @Column(length = 10)
    private String prioridad = "media";
}