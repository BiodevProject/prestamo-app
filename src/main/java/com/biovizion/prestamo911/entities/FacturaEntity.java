package com.biovizion.prestamo911.entities;

import jakarta.persistence.*;
import lombok.*;


@AllArgsConstructor
@NoArgsConstructor
@Entity
@Data
@Table(name = "factura")
public class FacturaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String date;

    private String buyer;

    private String seller;

    private String product;
    private Integer price;
}
