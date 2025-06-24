package com.biovizion.prestamo911.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;


@AllArgsConstructor
@NoArgsConstructor
@Entity
@Data
@Table(name = "Rol")
public class RolEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idRol;

    @Column (name = "nombre")
    private String nombreRol;

    @OneToMany(mappedBy = "rol")
    @JsonBackReference
    private List<UsuarioEntity> usuarios;
}
