package com.biovizion.prestamo911.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "usuario")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;

    private String apellido;

    private String codigo;

    @Column(unique = true)
    private String email;

    private String password;

    private String celular;

    private String direccion;

    private String rol;

    @Column(name = "codigo_verificacion")
    private String codigoVerificacion;

    @Column(name = "activo")
    private boolean activo;

    private String foto;

    @Column(name = "dui_delante")
    private String duiDelante;

    @Column(name = "dui_atras")
    private String duiAtras;

    private String nit;

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL)
    private List<CreditoEntity> creditos = new ArrayList<>();


}
